package fr.cyberdodo.zonemc.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fi.iki.elonen.NanoHTTPD;
import fr.cyberdodo.zonemc.entity.Zone;
import fr.cyberdodo.zonemc.service.ZoneManager;

import java.io.IOException;
import java.util.*;

public class WebServer extends NanoHTTPD {

    private final ZoneManager zoneManager;

    public WebServer(int port, ZoneManager zoneManager) throws IOException {
        super(port);
        this.zoneManager = zoneManager;
        start(SOCKET_READ_TIMEOUT, false);
        System.out.println("Web server running on port " + port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if ("/zones".equals(uri)) {
            return newFixedLengthResponse(Response.Status.OK, "application/json", getZonesJson());
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found");
    }

    private String getZonesJson() {
        Collection<Zone> zones = zoneManager.getZones().values();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<Map<String, Object>> zonesData = new ArrayList<>();
        for (Zone zone : zones) {
            Map<String, Object> zoneData = new HashMap<>();
            zoneData.put("name", zone.getName());
            zoneData.put("pos1", zone.getPos1());
            zoneData.put("pos2", zone.getPos2());
            zoneData.put("world", zone.getWorld());
            zoneData.put("requiredBlocks", zoneManager.getRequiredBlocks(zone.getName()));

            zonesData.add(zoneData);
        }

        return gson.toJson(zonesData);
    }

}
