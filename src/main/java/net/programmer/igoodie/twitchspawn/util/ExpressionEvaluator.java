package net.programmer.igoodie.twitchspawn.util;


import net.programmer.igoodie.twitchspawn.tslanguage.event.EventArguments;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionEvaluator {

	public static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\$\\{(.*?)}");

	public static String replaceExpressions(String input, Function<String, String> evaluator) {
		Matcher matcher = EXPRESSION_PATTERN.matcher(input);
		StringBuilder sb = new StringBuilder();
		int start = 0;

		while(matcher.find()) {
			String expression = matcher.group(1);

			// Append previous part
			sb.append(input, start, matcher.start());
			start = matcher.end();

			// Evaluate and append new value
			sb.append(evaluator.apply(expression));
		}

		// Append trailing chars
		sb.append(input, start, input.length());

		return sb.toString();
	}

	public static String fromArgs(String expression, EventArguments args) {
		switch(expression) {
			case "event" -> {
				return args.eventName;
			}
			case "message" -> {
				return JSONUtils.escape(args.message);
			}
			case "message_unescaped" -> {
				return args.message;
			}
			case "title" -> {
				return args.rewardTitle;
			}
			case "actor" -> {
				return args.actorNickname;
			}
			case "streamer" -> {
				return args.streamerNickname;
			}
		}

		if(expression.equals("amount") && args.donationAmount != 0.0)
			return String.valueOf(args.donationAmount);

		if(expression.equals("amount_i") && args.donationAmount != 0.0)
			return String.valueOf((int) args.donationAmount);

		if(expression.equals("amount_f") && args.donationAmount != 0.0)
			return String.format("%.2f", args.donationAmount);

		if(expression.equals("currency") && args.donationCurrency != null)
			return args.donationCurrency;

		if(expression.equals("months") && args.subscriptionMonths != 0)
			return String.valueOf(args.subscriptionMonths);

		if(expression.equals("tier") && args.subscriptionTier != -1)
			return args.subscriptionTier == 0 ? "Prime" : String.valueOf(args.subscriptionTier);

		if(expression.equals("gifted"))
			return String.valueOf(args.gifted);

		if(expression.equals("viewers") && args.viewerCount != 0)
			return String.valueOf(args.viewerCount);

		if(expression.equals("raiders") && args.raiderCount != 0)
			return String.valueOf(args.raiderCount);

		return switch(expression) {
			case "date" -> getDateFormat("dd-MM-yyyy", TimeZone.getDefault()).format(new Date());
			case "date_utc" -> getDateFormat("dd-MM-yyyy", TimeZone.getTimeZone("UTC")).format(new Date());
			case "time" -> getDateFormat("HH:mm:ss", TimeZone.getDefault()).format(new Date());
			case "time_utc" -> getDateFormat("HH:mm:ss", TimeZone.getTimeZone("UTC")).format(new Date());
			case "unix" -> String.valueOf(Instant.now().getEpochSecond());
			default -> null;
		};
	}

	private static DateFormat getDateFormat(String format, TimeZone timezone) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(timezone);
		return dateFormat;
	}

}
