package com.kunyan.gztelecom;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hcatalog.data.HCatRecord;
import org.apache.hcatalog.mapreduce.HCatInputFormat;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StockAnalysis {

    public StockAnalysis() {
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();

        String[] otherArgs = (new GenericOptionsParser(conf, args)).getRemainingArgs();
        if(otherArgs.length < 3) {
            System.err.println("Usage: TimeAndUrl <in> [<in>...] <out>");
            System.exit(2);
        }

        Job job = new Job(conf, "StockAnalysis");

        HCatInputFormat.setInput(job, otherArgs[0], otherArgs[1]);

        job.setJarByClass(StockAnalysis.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setInputFormatClass(HCatInputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
        System.exit(job.waitForCompletion(true)?0:1);
    }

    public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {

        public IntSumReducer() {}

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

            Iterator var5 = values.iterator();

            while(var5.hasNext()) {
                Text val = (Text)var5.next();
                context.write(key, val);
            }

        }
    }

    public static class TokenizerMapper extends Mapper<WritableComparable, HCatRecord, Text, Text> {

        private Text stockCode = new Text();
        private Text keyText = new Text();

        public TokenizerMapper() {}

        public void map(WritableComparable key,
                        HCatRecord value,
                        org.apache.hadoop.mapreduce.Mapper<WritableComparable, HCatRecord, Text, Text>.Context context) throws IOException, InterruptedException {

            String url = (String)value.get(0);
            String[] visitSearchPatterns = new String[]{"gw.*stock.*?(\\d{6})", "gubaapi.*code=(\\d{6})", "0033.*list.*(\\d{6}).*json", "platform.*symbol=\\w\\w(\\d{6})", "quote.*(\\d{6}).html", "tfile.*(\\d{6})/fs_remind", "dict.hexin.cn.*pattern=(\\S.*)", "suggest.eastmoney.com.*input=(.*?)&", "smartbox.gtimg.cn.*q=(.*?)&", "suggest3.sinajs.cn.*key=(((?!&name).)*)"};

            String content = "";
            List<Object> list = value.getAll();

            for (Object obj : list) {
                content += obj.toString() + "\t";
            }

            for(int i = 0; i < visitSearchPatterns.length; ++i) {

                Matcher matcher = Pattern.compile(visitSearchPatterns[i]).matcher(url);

                if(matcher.find()) {

                    this.stockCode.set(calculate(content));
                    this.keyText.set(String.valueOf(i));
                    context.write(this.keyText, this.stockCode);

                    break;
                }
            }

        }
    }

    public static String calculate(String source) {

        String base64EnStr = "";

        String password = "kunyannn";

        // Encrypt the text
        byte[] textEncrypted = encrypt(source.getBytes(),password);

        if (textEncrypted != null)
            base64EnStr = new BASE64Encoder().encode(textEncrypted);

        return base64EnStr;
    }

    /**
     * 加密
     * @param dataSource byte[]
     * @param password String
     * @return byte[]
     */
    public static  byte[] encrypt(byte[] dataSource, String password) {

        try {

            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password.getBytes());
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            //用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            //现在，获取数据并加密
            //正式执行加密操作

            return cipher.doFinal(dataSource);

        } catch (Throwable e) {

            e.printStackTrace();

        }

        return null;
    }

    /**
     * 解密
     * @param src byte[]
     * @param password String
     * @return byte[]
     * @throws Exception
     */
    public static byte[] decrypt(byte[] src, String password) throws Exception {

        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(password.getBytes());
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);

        // 真正开始解密操作
        return cipher.doFinal(src);
    }
}
