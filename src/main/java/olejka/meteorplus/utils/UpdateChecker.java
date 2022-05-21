package olejka.meteorplus.utils;

import olejka.meteorplus.MeteorPlus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class UpdateChecker {
	public boolean check() {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("https://raw.githubusercontent.com/TheSainEyereg/MeteorPlus/main/version.txt?token=GHSAT0AAAAAABQNRCML5RC4SZI2BQM7DE5CYUIPOXA");

		try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();

			String content = EntityUtils.toString(entity);
			if (!content.equals(MeteorPlus.version)) {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
