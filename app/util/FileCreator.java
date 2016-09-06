package util;

import play.mvc.Http;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Created by Павел on 22.05.2016.
 */
public class FileCreator {
    private FileCreator(){}

    public static String getExtension(Http.MultipartFormData.FilePart<File> file){
        return file.getContentType().split("/")[1];
    }

    public static Path createWebm(Http.MultipartFormData<File> body) throws IOException, InterruptedException {
        Http.MultipartFormData.FilePart<File> picture = body.getFile("picture");
        Http.MultipartFormData.FilePart<File> audio = body.getFile("audio");
        if (picture != null && audio != null) {
            File picFile = new File("storage/" + UUID.randomUUID().toString() + "." + getExtension(picture));
            File audioFile = new File("storage/" + UUID.randomUUID().toString() + "." + getExtension(audio));
            BufferedImage bufferedImage = ImageIO.read(picture.getFile());
            ImageIO.write(bufferedImage, getExtension(picture), picFile);
            Path p = audio.getFile().toPath();
            byte[] data = Files.readAllBytes(p);
            Files.write(audioFile.toPath(), data);
            String resFilePath =  UUID.randomUUID().toString() + ".webm";
            String command = getFfmpegCommand(audioFile.toPath(), picFile.toPath(),resFilePath);
            System.out.println(command);
            Process process = Runtime.getRuntime().exec(command, null, picFile.toPath().getParent().toAbsolutePath().toFile());
            InputStream in = process.getErrorStream();//
            int c;
            while ((c = in.read()) != -1)
            {
                System.out.print((char)c);
            }
            in.close();
            InputStream inStd = process.getInputStream();
            int k;
            while ((k = inStd.read()) != -1)
            {
                System.out.print((char)k);
            }
            inStd.close();
            process.waitFor();
            return Paths.get(resFilePath);
        }
        else {
            throw new RuntimeException("Files are null!");
        }
    }

    public static String getFfmpegCommand(Path audio, Path image, String result){
        return "ffmpeg -loop 1 -i " + image.getFileName().toString()
                + " -i " + audio.getFileName().toString() + " -shortest -c:v libvpx -c:a libvorbis " + result;
    }
}
