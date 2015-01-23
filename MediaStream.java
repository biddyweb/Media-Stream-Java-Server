import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.GZIPOutputStream;

import javax.activation.MimetypesFileTypeMap;

public class MediaStream {
	static Socket socket = null;
	// BufferedReader in=null;
	static String StatusLine = "HTTP/1.1 200 OK\r\n";
	static String ContentTypeLine = "Content-Type: audio/mp3\r\n";
	static String ContentLengthLine;

	public static void main(String args[]) throws IOException {
		ServerSocket s = new ServerSocket(5555);

		while (true) {
			try {
				socket = s.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				DataOutputStream dout = new DataOutputStream(
						socket.getOutputStream());
				String line = "", str = "";
				while (!(line = in.readLine()).equals("")) {
					str += line + "\n";

				}
				System.out.println(str);
				StartStream(in, dout, str);
			} catch (Exception e) {
				System.out.println("Error: " + e.toString());
			}

		}
	}

	public static void StartStream(BufferedReader in, DataOutputStream dout,
			String str) {
		try { // File f=new
				// File("D:/finished/V!ds/The Internets Own Boy The Story of Aaron Swartz (2014) 720p HDRip x264[bfmvrck].mkv");
			// File f = new File("D:/finished/V!ds/Akcent - Love Stoned.mp4");
		// File f=new File("D:/finished/V!ds/Bilal Saeed - 12 Saal.mp4");
	File f = new File("D:/finished/V!ds/Madcon - Beggin' - Street Dance 3D - Dance Mix.MP4");
			// File f=new File("D:/finished/Galliyan.mp3");
			FileInputStream fin = new FileInputStream(f);
			MimetypesFileTypeMap m = new MimetypesFileTypeMap();
			String mime = m.getContentType(f);

			StringBuilder sb = new StringBuilder();

			if (str.contains("Range:")) {
				long size = fin.available();
				ContentTypeLine = "Content-Type: " + mime + "\r\n";
				System.out.println(mime);

				
				StatusLine = "HTTP/1.1 206 OK\r\n";
				sb.append(StatusLine);

				sb.append(ContentTypeLine);
				// sb.append(contentEncoding);

				int p = str.indexOf("Range:");
				str = str.substring(p + 13, str.indexOf("-", p));
				System.out.println(str);
				long offset = Long.parseLong(str);
				byte[] ar = new byte[999999];
				long len = 0;
				long count = 0;
				int packet = 1;
				while (true) {
					count += fin.read();
					
					if (count >= offset)
						break;
				}
				System.out.println(count);
				sb.append("Accept-Ranges: bytes\r\n");
				sb.append("Range: bytes=" + count + "-" + size + "\r\n");
				ContentLengthLine = "Content-Length: " + (size-count) + "\r\n";
				sb.append(ContentLengthLine);
				
				sb.append("Content-Disposition: inline; filename=xxxxx.mp4\r\n");
               // sb.append("Connection: close\r\n\r\n");
				dout.writeBytes(sb.toString());
				dout.flush();
				while ((len = fin.read(ar)) != -1) {
					try {

						dout.write(ar, 0, (int) len);
						System.out.println("Packet no: " + packet
								+ "send,size= " + len);
						packet++;
						dout.flush();
					} catch (Exception e) {
						System.out.println("Write error: " + e.toString());
					}
				}
				System.out.println("DOne");

			} else {
				ContentTypeLine = "Content-Type: " + mime + "\r\n";
				System.out.println(mime);
				ContentLengthLine = "Content-Length: " + fin.available()
						+ "\r\n";
				String contentEncoding = "Content-Encoding: gzip\r\n";

				sb.append(StatusLine);

				sb.append(ContentTypeLine);
				// sb.append(contentEncoding);
				// sb.append("Connection: close\r\n");
				sb.append("Accept-Ranges: bytes\r\n");
				sb.append(ContentLengthLine);
				sb.append("Content-Disposition: inline; filename=xxxxx.mp4\r\n\r\n");

				dout.writeBytes(sb.toString());
				dout.flush();
				byte[] ar = new byte[999999];
				long len = 0;
				int packet = 1;
				while ((len = fin.read(ar)) != -1) {
					try {

						dout.write(ar, 0, (int) len);
						System.out.println("Packet no: " + packet
								+ "send,size= " + len);
						packet++;
						dout.flush();
					} catch (Exception e) {
						System.out.println("Write error: " + e.toString());
					}
				}
				System.out.println("DOne");
			}
		} catch (Exception e) {
			System.out.println(e.toString());

		}
	}
}
