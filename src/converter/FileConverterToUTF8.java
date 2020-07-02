package converter;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.mozilla.universalchardet.UniversalDetector;

public class FileConverterToUTF8 {
	public static void main(String[] args) throws IOException {

		if (args.length < 2) {
			System.err.println("usage >>> java -jar FileConverterToUTF8.jar [formFilePath] [toFilePath]");
			return;
		}

		String filePath = args[0];
		String targetFilePath = args[1];

		System.out.println(filePath + ", " + targetFilePath);

		String readData = readFileData2(filePath);

		System.out.println("---- data check ------------");
		System.out.println("readData >> ");
		System.out.println(readData);
		System.out.println("----------------------------");

		writeFileData(targetFilePath, readData);
	}

	private static void writeFileData(String targetFilePath, String readData) throws IOException {
		Writer fileWriter = new OutputStreamWriter(new FileOutputStream(targetFilePath), StandardCharsets.UTF_8);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

		bufferedWriter.write(readData.toCharArray());

		bufferedWriter.close();
		fileWriter.close();
	}

	public static String findFileEncoding(byte[] rawData) throws IOException {

		UniversalDetector detector = new UniversalDetector(null);

		byte[] dataBytes = rawData;
		while (!detector.isDone()) {
			detector.handleData(dataBytes);
		}

		detector.dataEnd();

		String encoding = detector.getDetectedCharset();

		if (encoding != null) {
			System.out.println("Detected encoding = " + encoding);
		} else {
			System.out.println("No encoding detected.");
		}

		detector.reset();

		return encoding;
	}

	private static String readFileData2(String filePath) throws IOException {

		final int BUFFER_SIZE = 8192;
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filePath));
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		UniversalDetector detector = new UniversalDetector(null);

		byte[] buffer = new byte[BUFFER_SIZE];
		int len = -1;

		while ((len = bufferedInputStream.read(buffer)) != -1) {
			stream.write(buffer, 0, len);
		}

		byte[] rawData = stream.toByteArray();

		String encoding = findFileEncoding(rawData);

		return new StringBuffer(Charset.forName(encoding).decode(ByteBuffer.wrap(rawData))).toString();
	}
}
