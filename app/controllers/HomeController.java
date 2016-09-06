package controllers;

import akka.stream.impl.io.OutputStreamSourceStage;
import play.api.mvc.MultipartFormData;
import play.mvc.*;

import util.FileCreator;
import views.html.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(index.render("Simply create your mus webm!"));
    }


    public  Result addPicture() throws IOException, InterruptedException {
        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        ok(index.render("Webm conv started"));
        Path webm = FileCreator.createWebm(body);
        response().setHeader("Content-Disposition","attachment; filename=" + webm.getFileName().toString());
        return ok(new FileInputStream(new File("storage\\" + webm.toString())));
    }
}
