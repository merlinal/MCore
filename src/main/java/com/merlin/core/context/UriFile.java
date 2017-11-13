package com.merlin.core.context;

import com.merlin.core.util.MVerify;

/**
 * 自定义uri
 *
 * @author zal
 */


public class UriFile {
    public String scheme;
    /**
     * 必传
     */
    public String host;
    /**
     * 必传
     */
    public String clazz;
    public String port;
    public String path;
    public String param;

    public String getKey() {
        StringBuffer sb = new StringBuffer();
        sb.append("scheme").append("://").append(host);
        if (!MVerify.isBlank(port)) {
            sb.append(":").append(port);
        }
        if (!MVerify.isBlank(path)) {
            sb.append("/").append(path);
        }
        return sb.toString();
    }


/*格式如下：
    [
        {
            "host" : "user",
            "class" : "com.merlin.note.user.UserListActivity",
            "param" : ["phone", "code"]
        },
        {
            "host" : "user",
            "class" : "com.merlin.note.user.UserDetailActivity",
            "path" : "detail",
            "port" : "8888",
            "param" : ["phone-手机号", "code"]
        }
    ]
*/
}
