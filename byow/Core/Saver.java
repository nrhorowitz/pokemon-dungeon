package byow.Core;

import java.io.*;

public class Saver implements Serializable {

    public static void saveWorld(String currWorld) {
        File f = new File("./save_data.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(currWorld);
        }  catch (FileNotFoundException e) {
            System.out.println("file not found");
            //System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            //System.exit(0);
        }
    }

    public static String loadWorld() {
        File f = new File("./save_data.txt");
        System.out.println(f.exists());
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                String returnString = (String) os.readObject();
                System.out.println(returnString);
                return returnString;
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                //System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                //System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                //System.exit(0);
            }
        }
        return ""; //just for it to compile
    }
}
