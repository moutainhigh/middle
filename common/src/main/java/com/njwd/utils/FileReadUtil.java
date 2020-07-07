package com.njwd.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
/**
 * @Description
 * @Author: shenhf
 * @Date: 2020/01/02 14:20
 */
public class FileReadUtil {
    /**
     * 功能：Java读取txt文件的内容 步骤：1：先获得文件句柄 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流 4：一行一行的输出。readline()。 备注：需要考虑的是异常情况
     *
     * @param filePath
     *            文件路径[到达文件:如： D:\aa.txt]
     * @return 将这个文件按照每一行切割成数组存放到list中。
     */
    public static List<String> readTxtFileIntoStringArrList(String filePath)
    {
        List<String> list = new ArrayList<String>();
        try
        {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists())
            { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        // 考虑到编码格式
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;

                while ((lineTxt = bufferedReader.readLine()) != null)
                {
                    list.add(lineTxt);
                }
                bufferedReader.close();
                read.close();
            }
            else
            {
                System.out.println("找不到指定的文件");
            }
        }
        catch (Exception e)
        {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 读取filePath的文件，将文件中的数据按照行读取到String数组中
     * @param filePath    文件的路径
     * @return            文件中一行一行的数据
     */
    public static String[] readToString(String filePath)
    {
        File file = new File(filePath);
        // 获取文件长度
        Long fileLength = file.length();
        byte[] fileContent = new byte[fileLength.intValue()];
        try
        {
            FileInputStream in = new FileInputStream(file);
            in.read(fileContent);
            in.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        String[] fileContentArr = new String(fileContent).split("\r\n");
        // 返回文件内容,默认编码
        return fileContentArr;
    }
    /**
    * 获取路径下文件
    * @param path    文件的路径
    * */
    public static ArrayList<String> getFiles(String path) {
        ArrayList<String> files = new ArrayList<String>();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                System.out.println("文     件：" + tempList[i]);
                files.add(tempList[i].toString());
            }
        }
        return files;
    }
    /**
     * 获取文件名
     * @param path    文件的路径
     * */
    public static String  getFilesName(String path) {
        File tempFile =new File( path.trim());

        String fileName = tempFile.getName();
        return fileName;
    }
    /**
     * 删除文件
     * @param path    文件的路径
     * */
    public static Boolean  deleteFile(String path) {
        Boolean flag = false;
        try {
            File file = new File(path);

            if(file.delete()) {
                flag = true;
                System.out.println( file.getName() + " is deleted!");
            }else {
                System.out.println("Delete operation is failed.");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

}
