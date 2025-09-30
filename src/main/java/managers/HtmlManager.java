package managers;

import tsetspawn.TSetSpawn;
import utils.ConfigFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HtmlManager {

    private final Path indexPath;

    public HtmlManager(TSetSpawn plugin) {
        ConfigFile indexFile = new ConfigFile("index.html", "html", plugin, false);
        indexFile.registerConfig();
        this.indexPath = indexFile.getFile().toPath();
    }

    public byte[] getHtmlBytes() {
        try {
            return Files.readAllBytes(indexPath);
        } catch (IOException e) {
            return new byte[0];
        }
    }
}

