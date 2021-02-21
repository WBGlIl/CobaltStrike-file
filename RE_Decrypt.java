package csdecrypt;

import common.SleevedResource;

import java.io.*;

public class Main {
    public static void saveFile(String filename,byte [] data)throws Exception{
        if(data != null){
            String filepath =filename;
            File file  = new File(filepath);
            if(file.exists()){
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data,0,data.length);
            fos.flush();
            fos.close();
        }
    }
    public static byte[] toByteArray(File f) throws IOException {

//         = new File(filename);
//        if (!f.exists()) {
//            throw new FileNotFoundException(filename);
//        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
    }
    public static void main(String[] var0) throws Exception {
        byte[] csdecrypt = new byte[]{1, -55, -61, 127, 102, 0, 0, 0, 100, 1, 0, 27, -27, -66, 82, -58, 37, 92, 51, 85, -114, -118, 28, -74, 103, -53, 6};

        SleevedResource.Setup(csdecrypt);
        byte[] var7=null;
        File file = new File("sleeve");		//获取其file对象
        File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中

        for(File files:fs){					//遍历File[]数组
            if(!files.isDirectory())		//若非目录(即文件)，则解密
                var7 = SleevedResource.readResource(toByteArray(files));
                saveFile("sleevedecrypt\\"+files.getName(),var7);
                System.out.println("解密成功:"+files);

        }



    }
}
