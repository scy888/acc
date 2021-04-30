package common;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.Base64;

public class DownloadUtils {
	/**
	 * 解决下载时，中文文件名称乱码问题<br>
	 * 
	 * @param request request对象
	 * @param filename 你想让用户保存的文件名称
	 */
	public static String encodeFilename(HttpServletRequest request, String filename){ 
		try{
			String agent = request.getHeader("User-Agent");
			if (agent.contains("MSIE")) {
				// IE浏览器
				filename = URLEncoder.encode(filename, "utf-8");
				filename = filename.replace("+", " ");
			} else if (agent.contains("Firefox")) {
				// 火狐浏览器
				//BASE64Encoder base64Encoder = new BASE64Encoder();
				Base64.Encoder base64Encoder = Base64.getEncoder();
				filename = base64Encoder.encodeToString(filename.getBytes("utf-8"));
				filename = "=?utf-8?B?"+ filename + "?=";
			} else {
				// 其它浏览器
				filename = URLEncoder.encode(filename, "utf-8");				
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return filename;
	}
}
