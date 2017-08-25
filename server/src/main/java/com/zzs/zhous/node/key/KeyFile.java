/**
 *
 */
package com.zzs.zhous.node.key;

/**
 * @author Administrator
 */
public class KeyFile extends Key {

    private static final String publickeyPrfix = "publickey";
    private static final String privatekeyPrfix = "privatekey";

    private String workFile;
    private String workFileKey;
    //private String workFileKeyId;
    private String backupFile;
    private String backupFileKey;
    //private String backupFileKeyId;
    private boolean isPriviatKey = false;




    public KeyFile(String fileName, String key, boolean isPrivateKey) {
        super.setType(Key.Type.RSA);
        workFile = fileName;
        workFileKey = key;
        this.isPriviatKey = isPrivateKey;
        //updateKeyId();
    }


    /**
     * 添加新的keyFile，
     * <p>
     * 注意，不能保证线程安全。最好单线程运行。
     *
     * @param fileName
     * @param key
     * @param isPriviatKey
     * @throws Exception
     */
    public void addKeyFile(String fileName, String key, boolean isPriviatKey) throws Exception {
        if (workFile == null) {
            workFile = fileName;
            workFileKey = key;
            this.isPriviatKey = !isPriviatKey;
        } else {
            if (this.isPriviatKey != isPriviatKey)
                throw new Exception("Key type mismatched");

            if (workFile.compareToIgnoreCase(fileName) < 0) {
                backupFile = workFile;
                backupFileKey = workFileKey;

                workFile = fileName;
                workFileKey = key;
            } else if (backupFile != null && backupFile.compareToIgnoreCase(fileName) < 0) {
                backupFile = fileName;
                backupFileKey = key;
            }
        }

        //updateKeyId();
    }



    public static String getMapKey(String userId, String appId, boolean isPublicKey) {
        String mapkey = null;
        if (isPublicKey)
            mapkey = userId + Key.separator + appId + Key.separator + publickeyPrfix;
        else
            mapkey = userId + Key.separator + appId + Key.separator + privatekeyPrfix;
        return mapkey;
    }

//	public static String getFullKey(String mapkey,String keyId){
//		return mapkey+Key.separator+keyId;
//	}


//	private void updateKeyId(){
//		if(workFileKey!=null){
//			String tmp[]=workFileKey.split("_");
//			workFileKeyId=tmp[tmp.length-1];
//		}
//		if(backupFileKey!=null){
//			String tmp[]=backupFileKey.split("_");
//			backupFileKeyId=tmp[tmp.length-1];
//		}
//	}


    public boolean contains(String key) {
        if ((workFileKey != null && workFileKey.equals(key))
                || (backupFileKey != null && backupFileKey.equals(key)))
            return true;
        else
            return false;
//		
//		if(workFileKey==null ) return false;
//		if(workFileKey.equals(key)) 
//			return true;
//		else if(backupFileKey==null)
//			return false;
//		else if(backupFileKey.equals(key))
//			return true;
//		else 
//			return false;
    }

//	private final String sparator="_";
//	private final String publicKeyFlag="_publickey";
//	private final String privateKeyFlag="_privatekey";
//	public String createKey(String userId,String appId,boolean isPublicKey){
//		String key=null;
//		if(isPublicKey)
//			key=userId+sparator+appId+publicKeyFlag;
//		else
//			key=userId+sparator+appId+privateKeyFlag;
//		return key;
//	}


    public String getKey(boolean isWorkKey) {
        if (isWorkKey)
            return workFileKey;
        else if (backupFileKey != null)
            return backupFileKey;
        else
            return workFileKey;

    }


    public String getFieName(String key) {
        if (key == null) return workFile;
        if (null != workFileKey && workFileKey.equals(key)) {
            return workFile;
        }
        if (null != backupFileKey && backupFileKey.equals(key)) {
            return backupFile;
        }

        return null;
    }

    public boolean isPriviatedKey() {
        return this.isPriviatKey;
    }


    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("\nid=" + super.toString())
                .append("\nwork   File=" + workFile + " Key=" + workFileKey)//+" Id="+workFileKeyId)
                .append("\nbackup File=" + backupFile + " Key=" + backupFileKey);//+" Id="+backupFileKeyId);

        return buf.toString();
    }


}
