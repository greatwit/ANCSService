package gz.lifesense.ancs.androidancs;

import android.util.Log;
import gz.lifesense.ancs.util.DataTools;


public class PedometerProtocol 
{

    /**
     * ��ѯ�豸��Ϣ
     * @return
     */
    public static byte[] askDeviceInfo()
    {

       String instruct= "A3080100000000AC";
        byte[] bArray = DataTools.decodeHex(instruct.toCharArray());

        return bArray;
    }
    public static byte[] sendMissingCall(){//01��δ������

        String instruct= "A3080201050000B3";
        byte[] bArray = DataTools.decodeHex(instruct.toCharArray());

        Log.i("COMING", "sendMissingCall!!!!");
        return bArray;
    }


   public static byte[] sendCommingCall(){//02����

       String instruct= "A3080202010000B0";//A3080202010000B0
       byte[] bArray = DataTools.decodeHex(instruct.toCharArray());

       Log.i("COMING", "sendCommingCall!!!!");
       return bArray;
   }
   
   public static byte[] sendCommingCallReject(){//04�ܽ�

       String instruct= "A3080204040000B5";
       byte[] bArray = DataTools.decodeHex(instruct.toCharArray());
       Log.i("COMING", "sendCommingCallReplay!!!!");

       return bArray;
   }
   public static byte[] sendCommingCallReplay(){//05�����ѽӻ��Ѳ鿴

       String instruct= "A3080205000000B2";
       byte[] bArray = DataTools.decodeHex(instruct.toCharArray());
       Log.i("COMING", "sendCommingCallReplay!!!!");

       return bArray;
   }
   
   public static byte[] openCommingCall(){

       String instruct= "A3080601000000B2";
       byte[] bArray = DataTools.decodeHex(instruct.toCharArray());
       Log.i("COMING", "openCommingCall!!!!");
       return bArray;
   }
   public static byte[] closeCommingCall(){

       String instruct= "A3080600000000B1";
       byte[] bArray = DataTools.decodeHex(instruct.toCharArray());
       Log.i("COMING", "closeCommingCall!!!!");
       return bArray;
   }
  


}
