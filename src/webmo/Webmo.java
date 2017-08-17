package webmo;

import java.util.Arrays;
import org.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * <a href=
 * "https://webmo.api-docs.io/v0.1.1">https://webmo.api-docs.io/v0.1.1</a>
 * 
 * @author progfay
 */
public class Webmo {
	/**
	 * 接続先アドレス。<br>
	 * 特に指定しない場合、 http://webmo.local となります。
	 */
	protected String address;

	/**
	 * 最後に実行したwebmoに関するHTTPリクエストのレスポンスのcontext bodyが保持されます。
	 */
	private String content;

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

	/**
	 * webmo.localをホストネームとして、Webmoを構築します。
	 */
	public Webmo() {
		this.address = "http://webmo.local/api";
	}

	/**
	 * ホストネームを指定して、Webmoを構築します。
	 * 
	 * @param hostName
	 *            指定するホストネーム
	 */
	public Webmo(String hostName) {
		this.address = "http://" + hostName + "/api";
	}

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

	/**
	 * 指定されたスピードで回転し続けます。
	 * 
	 * @param speed
	 *            回転速度[度/毎秒]
	 */
	public void rotate(int speed) {
		sendPostRequest("/rotate/forever", new BasicNameValuePair("speed", Integer.toString(speed)));
	}

	/**
	 * 絶対的に位置を指定して回転します。
	 * 
	 * @param degree
	 *            移動する角度の絶対位置
	 * @param speed
	 *            回転速度[度/毎秒]
	 * @param absRange
	 *            絶対位置の誤差許容値
	 */
	public void rotateTo(int degree, int speed, int absRange) {
		sendPostRequest("/rotate", new BasicNameValuePair("degree", Integer.toString(degree)),
				new BasicNameValuePair("speed", Integer.toString(speed)),
				new BasicNameValuePair("absRange", Integer.toString(absRange)),
				new BasicNameValuePair("absolute", Boolean.toString(true)));
	}

	/**
	 * 相対的に位置を指定して回転します。
	 * 
	 * @param degree
	 *            移動する角度の相対位置
	 * @param speed
	 *            回転速度[度/毎秒]
	 */
	public void rotateBy(int degree, int speed) {
		sendPostRequest("/rotate", new BasicNameValuePair("degree", Integer.toString(degree)),
				new BasicNameValuePair("speed", Integer.toString(speed)),
				new BasicNameValuePair("absolute", Boolean.toString(false)));
	}

	/**
	 * Webmoの回転を停止させます。<br>
	 * シャフトはロックしません。自由に回転できます。
	 * 
	 * @param smooth
	 *            停止時にスムージングを行うか
	 */
	public void stop(boolean smooth) {
		sendPostRequest("/stop", new BasicNameValuePair("smooth", Boolean.toString(smooth)));
	}

	/**
	 * Webmoの回転を停止させ、シャフトをロックします。
	 * 
	 * @param smooth
	 *            停止時にスムージングを行うか
	 */
	public void lock(boolean smooth) {
		sendPostRequest("/lock", new BasicNameValuePair("smooth", Boolean.toString(smooth)));
	}

	/**
	 * Webmoに内蔵されているスムージングのかかった回転センサの値を取得します。
	 * 
	 * @return スムージングのかかった回転センサの値
	 */
	public int rotation() {
		this.sendGetRequest("/sensor/rotation");
		JSONObject json = new JSONObject(this.content);
		return json.getInt("rotation") * 360 / 4096;
	}

	/**
	 * Webmoに内蔵されているスムージングのかかっていない回転センサの値を取得します。
	 * 
	 * @return スムージングのかかっていない回転センサの値
	 */
	public int rotation_raw() {
		this.sendGetRequest("/sensor/rotation");
		JSONObject json = new JSONObject(this.content);
		return json.getInt("rotation_raw") * 360 / 4096;
	}

	/**
	 * Webmo内部に内蔵されている温度センサの値を取得します。
	 * 
	 * @return 温度センサの値[℃]
	 */
	public float temperature() {
		this.sendGetRequest("/sensor/temperature");
		JSONObject json = new JSONObject(this.content);
		return (float) json.getDouble("temperature");
	}

	/**
	 * 最後に実行したwebmoに関するHTTPリクエストのレスポンスのcontext bodyを取得します。
	 * 
	 * @return 最後に実行したwebmoに関するHTTPリクエストのレスポンスのcontext body
	 */
	public String getContent() {
		return this.content;
	}

	private void sendGetRequest(String action) {
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();

			HttpGet httpGet = new HttpGet(this.address + action);
			httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded");

			HttpResponse response = httpClient.execute(httpGet);
			this.content = EntityUtils.toString(response.getEntity());

			HttpEntity entity = response.getEntity();
			if (entity != null)
				EntityUtils.consume(entity);

			httpClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendPostRequest(String action, BasicNameValuePair... nameValuePairs) {
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(this.address + action);

			httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
			httpPost.setEntity(new UrlEncodedFormEntity(Arrays.asList(nameValuePairs), "ISO-8859-1"));

			HttpResponse response = httpClient.execute(httpPost);
			this.content = EntityUtils.toString(response.getEntity());

			HttpEntity entity = response.getEntity();
			if (entity != null)
				EntityUtils.consume(entity);

			httpClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
