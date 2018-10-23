package com.example.liveready;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
public class WebController {

    AtomicBoolean atomicReady = new AtomicBoolean(true);
    AtomicBoolean atomicLive = new AtomicBoolean(true);

    File fileReady = new File("/tmp/ready");
    File fileLive= new File("/tmp/live");
    long startTime = 0;

    @EventListener(ApplicationReadyEvent.class)
    public void readyInit() throws IOException {
        atomicReady.set(true);
        atomicLive.set(true);
        createFile(fileReady,true);
        createFile(fileLive,true);
        startTime = System.currentTimeMillis();
    }

    @RequestMapping("/")
    @ResponseBody
    public Object simple(HttpServletResponse response) {
        return "ok"+getHostname();
    }

    @RequestMapping("/ready")
    @ResponseBody
    public Object ready(HttpServletResponse response) {
        if (!atomicReady.get()) {
            response.setStatus(500);
            return "500" +getHostname();
        }
        return "finish" +getHostname();
    }

    @RequestMapping("/changeready")
    @ResponseBody
    public Object changeReady() throws IOException {
        boolean bool = !atomicReady.get();
        atomicReady.set(bool);
        createFile(fileReady, bool);
        return "ready" +getHostname();
    }

    @RequestMapping("/live")
    @ResponseBody
    public Object live(HttpServletResponse response) {
        if (!atomicLive.get()) {
            response.setStatus(500);
            return "500" +getHostname();
        }
        return "finish" +getHostname();
    }

    @RequestMapping("/changelive")
    @ResponseBody
    public Object liveReady() throws IOException {
        boolean bool = !atomicLive.get();
        atomicLive.set(bool);
        createFile(fileLive, bool);
        return "ready"+getHostname();
    }

    private String getHostname() {
        return " "+System.getenv("HOSTNAME")+ " "+ (System.currentTimeMillis() -startTime);
    }

    private void createFile(File file, boolean bool) throws IOException {
        System.out.println("change "+ file+ " "+ bool);
        if (!bool) {
            try {
                file.delete();
            } catch (Exception e) {
            }
        }
        else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
