package gz.lifesense.ancs.androidancs;

import android.util.Log;
import gz.lifesense.ancs.util.DataTools;


public class PedometerProtocol 
{

    /**
     * 查询设备信息
     * @return
     */
    public static byte[] askDeviceInfo()
    {

       String instruct= "A3080100000000AC";
        byte[] bArray = DataTools.decodeHex(instruct.toCharArray());

        return bArray;
    }
    public static byte[] sendMissingCall(){//01有未接来电

        String instruct= "A3080201050000B3";
        byte[] bArray = DataTools.decodeHex(instruct.toCharArray());

        Log.i("COMING", "sendMissingCall!!!!");
        return bArray;
    }


   public static byte[] sendCommingCall(){//02来电

       String instruct= "A3080202010000B0";
       byte[] bArray = DataTools.decodeHex(instruct.toCharArray());

       Log.i("COMING", "sendCommingCall!!!!");
       return bArray;
   }
   
   public static byte[] sendCommingCallReject(){//04拒接

       String instruct= "A3080204040000B5";
       byte[] bArray = DataTools.decodeHex(instruct.toCharArray());
       Log.i("COMING", "sendCommingCallReplay!!!!");

       return bArray;
   }
   public static byte[] sendCommingCallReplay(){//05来电已接或已查看

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
