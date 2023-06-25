package com.PlayerCoder1;

import net.runelite.api.events.ChatMessage;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

@PluginDescriptor(
		name = "AI Translation Plugin"
)
public class TranslationPlugin extends Plugin
{
	private static final int MAX_MESSAGES = 10;

	private final LinkedList<String> lastMessages = new LinkedList<>();
	private OkHttpClient client;
	private TranslationPanel panel;
	private NavigationButton navButton;

	@Inject
	private ClientToolbar clientToolbar;

	@Override
	protected void startUp() throws Exception
	{
		client = new OkHttpClient();

		panel = new TranslationPanel(this);
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "panel_icon.png");
		navButton = NavigationButton.builder()
				.tooltip("Translation")
				.icon(icon)
				.priority(5)
				.panel(panel)
				.build();
		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		client = null;
		clientToolbar.removeNavigation(navButton);
	}

	public List<String> getLastMessages()
	{
		return lastMessages;
	}

	public String translateText(String originalText, String targetLanguage) throws IOException
	{
		String url = "https://api.mymemory.translated.net/get?q=" + URLEncoder.encode(originalText, "UTF-8") + "&langpair=en|" + targetLanguage;

		Request request = new Request.Builder()
				.url(url)
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

			JSONObject jsonResponse = new JSONObject(response.body().string());
			return jsonResponse.getJSONObject("responseData").getString("translatedText");
		}
	}



	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		String originalMessage = chatMessage.getMessage();
		String playerName = chatMessage.getName();

		new Thread(() -> {
			try {
				String translatedMessage = translateText(originalMessage, "es");


				SwingUtilities.invokeLater(() -> {
					lastMessages.addFirst(playerName + ": " + translatedMessage);
					while (lastMessages.size() > MAX_MESSAGES) {
						lastMessages.removeLast();
					}


					panel.updateMessages();
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
}