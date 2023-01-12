package com.zewsic.msm_server;

import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.requests.ExtensionRequest;
import sfs2x.client.requests.LoginRequest;
import sfs2x.client.util.ConfigData;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.zewsic.msm_server.MainExtension.ROOT;
import static com.zewsic.msm_server.UsersManager.save_user_data;
import static com.zewsic.msm_server.utils.*;

public class MSMClient {

    HashMap<String, String> auth_args = new HashMap<>();
    SmartFox sfs;

    String client_version = "3.8.1";
    String auth_token = "352cc208-381b-4dda-907d-d718833fe5c5";

    String device1 = "Mix";
    String device2 = "Xiaomi";

    String user_id = "";
    String user_token = "";
    String server_id = "";
    String server_ip = "";
    String update_url = "";
    String type = "";

    SFSObject friends;
    SFSObject friend;
    MainExtension x;

    public String auth(String mail,  String password) {
        auth_args.put("l", "1");
        auth_args.put("g", "1");
        auth_args.put("access_key", auth_token);
        auth_args.put("u", mail);
        auth_args.put("p", password);
        auth_args.put("t", "email");
        auth_args.put("msm_anon_converted", "1");
        auth_args.put("update_device", "1");
        auth_args.put("auth_version", "2.0.0");
        auth_args.put("refresh_token", "1");
        auth_args.put("device_model", device1);
        auth_args.put("device_vendor", device2);
        auth_args.put("platform", "android");
        auth_args.put("client_version", client_version);

        String resp = POST("https://auth.bbbgame.net/auth/api/token?" + ParamsBuilder(auth_args));
        try {
            SFSObject data = (SFSObject) SFSObject.newFromJsonData(resp);
            user_id = data.getSFSArray("user_game_id").getUtfString(0);
            user_token = data.getUtfString("access_token");

            return user_id + "|" + user_token;
        } catch (Exception e) {
            return resp;
        }
    }
    public String locateServer() {
        HashMap<String, String> h = new HashMap<>();
        h.put("Authorization", user_token);
        String resp = POST("https://msm-auth.bbbgame.net/pregame_setup.php?" + ParamsBuilder(auth_args), h);
        try {
            SFSObject data = (SFSObject) SFSObject.newFromJsonData(resp);
            server_id = data.getInt("serverId").toString();
            server_ip = data.getUtfString("serverIp");
            update_url = data.getUtfString("contentUrl");

            return server_ip;
        } catch (Exception e) {
            return resp;
        }
    }

    public void authInServer(String type) {
        ConfigData cfg = new ConfigData();
        cfg.setHost(server_ip);
        cfg.setPort(9933);
        cfg.setZone("MySingingMonsters");
        cfg.setDebug(false);

        this.type = type;

        // Set up event handlers
        sfs = new SmartFox();
        sfs.addEventListener(SFSEvent.HANDSHAKE, this::onConnection);
        sfs.addEventListener(SFSEvent.EXTENSION_RESPONSE, this::onResponse);
        // Connect to server
        sfs.connect(cfg);
    }

    public SFSObject get_friend(String bbbid,MainExtension x) {
        this.friend = null;
        this.friends = null;
        x.trace("Parsing id");
        this.x = x;
        long id;
        try {
            id = (long) NumberFormat.getInstance().parse(bbbid);
        } catch (ParseException e) {
            id=0;
        }
        x.trace(id);
        long fid = 0;

        x.trace("Adding friend");
        SFSObject a = new SFSObject();
        a.putLong("friend_id",id);
        sfs.send(new ExtensionRequest("gs_add_friend", a));
        x.trace("Getting friends list");
        sfs.send(new ExtensionRequest("gs_get_friends", new SFSObject()));
        try {TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {throw new RuntimeException(e);}
        x.trace("Finding friend");
        SFSArray friends = (SFSArray) this.friends.getSFSArray("friends");
        x.trace(friends);
        for (int i=0;i<friends.size();i++) {
            SFSObject friendx = (SFSObject) friends.getSFSObject(i);
            x.trace(friendx.getLong("bbb_id"));
            if (friendx.getLong("bbb_id")==id) {
                fid = friendx.getInt("user_id");
                x.trace("DONE");
                break;
            }
        }

        x.trace("Getting friend object");
        a = new SFSObject();
        a.putLong("user_id",fid);
        sfs.send(new ExtensionRequest("gs_get_friend_visit_data", a));
        try {TimeUnit.SECONDS.sleep(4);} catch (InterruptedException e) {throw new RuntimeException(e);}
        x.trace("Done!");
        return (SFSObject) this.friend.getSFSObject("friend_object");
    }

    private int getMonsterId(int monster_id, String type) {
        switch (type) {
            case "simple":
                return monster_id;
            case "rare": {
                SFSArray rares = (SFSArray) getSFSFromJson(new File(ROOT + "db_files" + File.separator + "gs_rare_monster_data.json")).getSFSArray("rare_monster_data");
                for (int i = 0; i < rares.size(); i++) {
                    SFSObject rare = (SFSObject) rares.getSFSObject(i);
                    if (rare.getInt("common_id") == monster_id) {
                        return rare.getInt("rare_id");
                    }
                }
                break;
            }
            case "epic": {
                SFSArray epics = (SFSArray) getSFSFromJson(new File(ROOT + "db_files" + File.separator + "gs_epic_monster_data.json")).getSFSArray("epic_monster_data");
                for (int u = 0; u < epics.size(); u++) {
                    SFSObject epic = (SFSObject) epics.getSFSObject(u);
                    if (epic.getInt("common_id") == monster_id) {
                        return epic.getInt("epic_id");
                    }
                }
                break;
            }
        }
        return -1;
    }

    private void onResponse(BaseEvent baseEvent) {
        var params = baseEvent.getArguments();
        String cmd  = params.get("cmd").toString();
        SFSObject args = (SFSObject) params.get("params");
        MainExtension.con.trace("MSMClient: "+cmd);
        MainExtension.con.trace("MSMClient: "+args);

        switch (cmd) {

            case "gs_initialized":
                if ("load_dbs".equals(type)) {
                    sfs.send(new ExtensionRequest("db_monster", new SFSObject()));
                    sfs.send(new ExtensionRequest("db_structure", new SFSObject()));
                    sfs.send(new ExtensionRequest("gs_rare_monster_data", new SFSObject()));
                    sfs.send(new ExtensionRequest("gs_epic_monster_data", new SFSObject()));
                    sfs.send(new ExtensionRequest("gs_player", new SFSObject()));
                    sfs.send(new ExtensionRequest("db_island", new SFSObject()));
                }
                break;
            case "db_monster": {
                int chunk = args.getInt("chunk");
                args.putInt("numChunks", args.getInt("numChunks"));

                SFSArray mlist = (SFSArray) args.getSFSArray("monsters_data");
                SFSArray mlist2 = new SFSArray();
                for (int m = 0; m < mlist.size(); m++) {
                    SFSObject monster = (SFSObject) mlist.getSFSObject(m);
                    monster.putInt("view_in_market", 1);
                    monster.putBool("box_monster", false);
                    monster.putUtfString("entity_type",  "monster");
                    monster.putSFSArray("keywords", new SFSArray());
                    monster.removeElement("keywords");
                    mlist2.addSFSObject(monster);
                }

                args.putSFSArray("monsters_data", mlist2);


                if (chunk == 1)
                    putSFSToJson(new File(ROOT + "db_files" + File.separator + "db_monster.json"), args);
                else
                    putSFSToJson(new File(ROOT + "db_files" + File.separator + "db_monster_" + chunk + ".json"), args);
                break;
            }
            case "db_island": {
                SFSArray islands = (SFSArray) args.getSFSArray("islands_data");
                SFSArray new_islands = new SFSArray();
                for (int i = 0; i < islands.size(); i++) {
                    SFSObject island = (SFSObject) islands.getSFSObject(i);
                    if (island.getUtfString("name").equals("ISLAND_TRIBAL")) {
                        SFSArray monsters = (SFSArray) island.getSFSArray("monsters");
                        SFSArray new_monsters = new SFSArray();

                        for (int m = 0; m < monsters.size(); m++) {
                            SFSObject monster = (SFSObject) monsters.getSFSObject(m);
                            int id = monster.getInt("monster_id");

                            //simple monster
                            SFSObject simple_monster = new SFSObject();
                            int simple_id = getMonsterId(id, "simple");
                            if (simple_id != -1) {
                                simple_monster.putInt("monster", simple_id);
                                new_monsters.addSFSObject(simple_monster);
                            }

                            //rare monster
                            SFSObject rare_monster = new SFSObject();
                            int rare_id = getMonsterId(id, "rare");
                            if (rare_id != -1) {
                                rare_monster.putInt("monster", rare_id);
                                new_monsters.addSFSObject(rare_monster);
                            }

                            //epic monster
                            SFSObject epic_monster = new SFSObject();
                            int epic_id = getMonsterId(id, "epic");
                            if (epic_id != -1) {
                                epic_monster.putInt("monster", epic_id);
                                new_monsters.addSFSObject(epic_monster);
                            }
                        }

                        island.putSFSArray("monsters", new_monsters);
                    }
                    new_islands.addSFSObject(island);
                }
                args.putSFSArray("islands_data", new_islands);
                putSFSToJson(new File(ROOT + "db_files" + File.separator + "db_island.json"), args);
                break;
            }
            case "db_structure": {
                int chunk = args.getInt("chunk");

                SFSArray mlist = (SFSArray) args.getSFSArray("structures_data");
                SFSArray newlist = new SFSArray();
                for (int m = 0; m < mlist.size(); m++) {
                    SFSObject monster = (SFSObject) mlist.getSFSObject(m);
                    monster.putInt("view_in_market", 1);
                    monster.putUtfString("allowed_on_island", "[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32]");

                    monster.putInt("view_in_starmarket", 1);
                    monster.putInt("view_in_market", 1);


                    newlist.addSFSObject(monster);
                }

                args.putSFSArray("structures_data", newlist);


                if (chunk == 1)
                    putSFSToJson(new File(ROOT + "db_files" + File.separator + "db_structure.json"), args);
                else
                    putSFSToJson(new File(ROOT + "db_files" + File.separator + "db_structure_" + chunk + ".json"), args);
                break;
            }
            case "gs_rare_monster_data": {
                putSFSToJson(new File(ROOT + "db_files" + File.separator + "gs_rare_monster_data.json"), args);
                break;
            }
            case "gs_epic_monster_data": {
                putSFSToJson(new File(ROOT + "db_files" + File.separator + "gs_epic_monster_data.json"), args);
                break;
            }
            case "gs_get_friends":
                friends = args;
                break;
            case "gs_get_friend_visit_data":
                friend = args;
                break;
            default:
                putSFSToJson(new File(ROOT + "saves" + File.separator + cmd + ".json"), args);
                break;
        }
    }

    private void onConnection(BaseEvent baseEvent) {
        SFSObject LoginData = new SFSObject();
        LoginData.putLong("last_updated", 0);
        LoginData.putUtfString("raw_device_id", "3A5B55581415CB12");
        LoginData.putUtfString("client_device", "Xiaomi Redmi note 11S");
        LoginData.putUtfString("last_update_version", "3.6.0");
        LoginData.putUtfString("client_os", "10");
        LoginData.putUtfString("client_platform", "android");
        LoginData.putUtfString("client_version", "3.7.1");
        LoginData.putUtfString("token", user_token);
        LoginData.putUtfString("access_key", "193c1fc4-0134-4fc3-a662-fc6fc9f3a645");

        sfs.send(new LoginRequest(user_id, null, "MySingingMonsters", LoginData));
    }

    public static SFSObject processRequest(String cmd, SFSObject params) {
        SFSObject ext = new SFSObject();
        switch (cmd) {
            case "gs_player":
                SFSObject user = (SFSObject) params.getSFSObject("player_object");
                SFSArray islands = (SFSArray) user.getSFSArray("islands");
                for (int i = 0; i < islands.size();i++) {
                    SFSObject island = (SFSObject) islands.getSFSObject(i);
                    SFSArray monsters = (SFSArray) island.getSFSArray("monsters");
                    for (int u = 0; u < monsters.size();u++) {
                        SFSObject c = (SFSObject) monsters.getSFSObject(u);

                        c.putUtfString("name", "Made by @msm_hacks");
                        c.putInt("level", 21);

                        monsters.removeElementAt(u);
                        monsters.addSFSObject(c);
                    }
                    island.putSFSArray("monsters", monsters);
                    islands.removeElementAt(i);
                    islands.addSFSObject(island);
                }
                user.putSFSArray("islands", islands);
                ext.putSFSObject("player_object", user);
                break;
            default:
                ext = params;
                break;
        }
        return ext;
    }
}
