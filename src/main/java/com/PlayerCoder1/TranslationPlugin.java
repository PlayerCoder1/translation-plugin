package com.PlayerCoder1;

import net.runelite.api.events.ChatMessage;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@PluginDescriptor(
		name = "Translation Plugin",
		description = "Translates everything in the chatbox from English to Spanish",
		tags = {"Translation","English","Spanish","Translator"}
)
public class TranslationPlugin extends Plugin {
	private static final int MAX_MESSAGES = 14;

	private final LinkedList<String> lastMessages = new LinkedList<>();
	private ExecutorService executorService;
	private TranslationPanel panel;
	private NavigationButton navButton;

	@Inject
	private OkHttpClient client;

	@Inject
	private ClientToolbar clientToolbar;

	@Override
	protected void startUp() {
		executorService = Executors.newFixedThreadPool(10);

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
	protected void shutDown() {
		executorService.shutdown();
		clientToolbar.removeNavigation(navButton);
	}
	public static class ApiLimitExceededException extends Exception {
		public ApiLimitExceededException(String message) {
			super(message);
		}
	}
	public List<String> getLastMessages() {
		return lastMessages;
	}

	public String translateText(String originalText, String targetLanguage) throws IOException, ApiLimitExceededException {
		String url = "https://api.mymemory.translated.net/get?q=" + URLEncoder.encode(originalText, StandardCharsets.UTF_8) + "&langpair=en|" + targetLanguage;

		Request request = new Request.Builder()
				.url(url)
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				if (response.code() == 429) {

					throw new ApiLimitExceededException("API usage limit exceeded");
				}
				throw new IOException("Unexpected code " + response);
			}

			ResponseBody responseBody = response.body();
			if (responseBody != null) {
				JSONObject jsonResponse = new JSONObject(responseBody.string());
				return jsonResponse.getJSONObject("responseData").getString("translatedText");
			} else {
				throw new IOException("Response body is null");
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage) {
		String originalMessage = chatMessage.getMessage();
		String playerName = chatMessage.getName();

		executorService.submit(() -> {
			try {
				String translatedMessage = translateText(originalMessage, "es");

				SwingUtilities.invokeLater(() -> {
					lastMessages.addFirst(playerName + ": " + translatedMessage);
					while (lastMessages.size() > MAX_MESSAGES) {
						lastMessages.removeLast();
					}

					panel.updateMessages();
				});
			} catch (ApiLimitExceededException e) {
				SwingUtilities.invokeLater(() -> {
					JOptionPane.showMessageDialog(null, "You have reached the 5000 words for today, please wait 24 hours to use the plugin again", "Error", JOptionPane.ERROR_MESSAGE);
					shutDown();
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}