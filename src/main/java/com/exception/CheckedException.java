package com.exception;

import cn.hutool.core.collection.CollectionUtil;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Objects;

/**
 * @author renbo
 * @date 2024-04-10
 */
@NoArgsConstructor
public class CheckedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CheckedException(String message) {
		super(message);
	}

	public CheckedException(Throwable cause) {
		super(cause);
	}

	public CheckedException(String message, Throwable cause) {
		super(message, cause);
	}

	public CheckedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}


	public static void checkNull(Object obj, String message) {
		if (Objects.isNull(obj)) {
			throw new CheckedException(message);
		}
	}

	public static void checkNotNull(Object obj, String message) {
		if (!Objects.isNull(obj)) {
			throw new CheckedException(message);
		}
	}

	public static void checkEmpty(String obj, String message) {
		if (org.springframework.util.StringUtils.isEmpty(obj)) {
			throw new CheckedException(message);
		}
	}

	public static void doThrow(String message) {
		throw new CheckedException(message);
	}

	public static void doThrow(String message, Object... args) {
		String formattedMessage = formatMessage(message, args);
		throw new CheckedException(formattedMessage);
	}

	private static String formatMessage(String message, Object... args) {
		if (args == null || args.length == 0) {
			return message;
		}

		String result = message;
		for (Object arg : args) {
			result = result.replaceFirst("\\{\\}", String.valueOf(arg));
		}
		return result;
	}

	public static void checkEmpty(Collection obj, String message) {
		if (CollectionUtil.isEmpty(obj)) {
			throw new CheckedException(message);
		}
	}
}
