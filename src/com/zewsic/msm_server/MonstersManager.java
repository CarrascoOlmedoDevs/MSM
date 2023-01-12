package com.zewsic.msm_server;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.Random;

import static com.zewsic.msm_server.UsersManager.load_user_data;
import static com.zewsic.msm_server.UsersManager.save_user_data;

public class MonstersManager {

    // {"pos_y":27,"volume":1.0,"pos_x":24,"user_monster_id":1}
    static SFSObject[] move_monster(String user_id, SFSObject input) {
        int x = input.getInt("pos_x");
        int y = input.getInt("pos_y");
        double s = input.getDouble("volume");
        long id = input.getLong("user_monster_id");

        SFSObject user_ = load_user_data(user_id);
        SFSObject user = (SFSObject) user_.getSFSObject("player_object");
        SFSArray islands = (SFSArray) user.getSFSArray("islands");
        for (int i = 0; i < islands.size();i++) {
            SFSObject island = (SFSObject) islands.getSFSObject(i);
            SFSArray monsters = (SFSArray) island.getSFSArray("monsters");
            SFSArray monsters_ = new SFSArray();
            for (int u = 0; u < monsters.size();u++) {
                SFSObject structure = (SFSObject) monsters.getSFSObject(u);

                Long structure_id = structure.getLong("user_monster_id");
                if (structure_id == id) {
                    structure.putInt("pos_x", x);
                    structure.putInt("pos_y", y);
                    structure.putDouble("volume", s);
                }
                monsters_.addSFSObject(structure);
            }
            island.putSFSArray("monsters", monsters_);
            islands.removeElementAt(i);
            islands.addSFSObject(island);
        }
        user.putSFSArray("islands", islands);
        user_.putSFSObject("player_object", user);
        save_user_data(user_id, user_);

        SFSObject resp = new SFSObject();
        resp.putBool("success", true);

        SFSObject resp2 = new SFSObject();
        resp2.putInt("pos_x", x);
        resp2.putInt("pos_y", y);
        resp2.putDouble("volume", s);
        resp2.putLong("user_monster_id", id);

        SFSObject[] resps =  new SFSObject[]{resp,resp2};

        return resps; //{"success":true}  gs_update_monster {"pos_x":24,"user_monster_id":1,"volume":1.0,"pos_y":27}
    }
    static SFSObject[] flip_monster(String user_id, SFSObject input) {
        long id = input.getLong("user_monster_id");
        boolean flip = input.getBool("flipped");

        SFSObject user_ = load_user_data(user_id);
        SFSObject user = (SFSObject) user_.getSFSObject("player_object");
        SFSArray islands = (SFSArray) user.getSFSArray("islands");
        for (int i = 0; i < islands.size();i++) {
            SFSObject island = (SFSObject) islands.getSFSObject(i);
            SFSArray monsters = (SFSArray) island.getSFSArray("monsters");
            for (int u = 0; u < monsters.size();u++) {
                SFSObject structure = (SFSObject) monsters.getSFSObject(u);

                Long structure_id = structure.getLong("user_monster_id");
                if (structure_id == id) {
                    structure.putInt("flip", flip?1:0);
                }
                monsters.removeElementAt(u);
                monsters.addSFSObject(structure);
            }
            island.putSFSArray("monsters", monsters);
            islands.removeElementAt(i);
            islands.addSFSObject(island);
        }
        user.putSFSArray("islands", islands);
        user_.putSFSObject("player_object", user);
        save_user_data(user_id, user_);

        SFSObject resp = new SFSObject();
        resp.putBool("success", true);

        SFSObject resp2 = new SFSObject();
        resp2.putInt("flip", flip?1:0);
        resp2.putLong("user_monster_id", id);

        SFSObject[] resps =  new SFSObject[]{resp,resp2};

        return resps; //{"success":true}  gs_update_monster {"pos_x":24,"user_monster_id":1,"volume":1.0,"pos_y":27}
    }
    // gs_sell_monster {"pure_destroy":false,"user_monster_id":82536}
    static SFSObject sell_monster(String user_id, SFSObject input) {
        long id = input.getLong("user_monster_id");

        SFSObject user_ = load_user_data(user_id);
        SFSObject user = (SFSObject) user_.getSFSObject("player_object");
        SFSArray islands = (SFSArray) user.getSFSArray("islands");
        for (int i = 0; i < islands.size();i++) {
            SFSObject island = (SFSObject) islands.getSFSObject(i);
            SFSArray monsters = (SFSArray) island.getSFSArray("monsters");
            for (int u = 0; u < monsters.size();u++) {
                SFSObject structure = (SFSObject) monsters.getSFSObject(u);

                Long structure_id = structure.getLong("user_monster_id");
                monsters.removeElementAt(u);
                if (structure_id != id) {
                    monsters.addSFSObject(structure);
                }

            }
            island.putSFSArray("monsters", monsters);
            islands.removeElementAt(i);
            islands.addSFSObject(island);
        }
        user.putSFSArray("islands", islands);
        user_.putSFSObject("player_object", user);
        save_user_data(user_id, user_);

        //SFSObject resp = new SFSObject();
        //resp.putBool("success", true);

        SFSObject resp = (SFSObject) SFSObject.newFromJsonData("{\"user_monster_id\":"+id+",\"properties\":[],\"success\":true}");

        return resp; //{"success":true}
    }

    // {"name":"Test 2","user_monster_id":27266}
    static SFSObject name_monster(String user_id, SFSObject input) {
        long id = input.getLong("user_monster_id");
        String name = input.getUtfString("name");

        SFSObject user_ = load_user_data(user_id);
        SFSObject user = (SFSObject) user_.getSFSObject("player_object");
        SFSArray islands = (SFSArray) user.getSFSArray("islands");
        for (int i = 0; i < islands.size();i++) {
            SFSObject island = (SFSObject) islands.getSFSObject(i);
            SFSArray monsters = (SFSArray) island.getSFSArray("monsters");
            for (int u = 0; u < monsters.size();u++) {
                SFSObject structure = (SFSObject) monsters.getSFSObject(u);

                Long structure_id = structure.getLong("user_monster_id");

                if (structure_id == id) {
                    structure.putUtfString("name", name);
                }
                monsters.removeElementAt(u);
                monsters.addSFSObject(structure);
            }
            island.putSFSArray("monsters", monsters);
            islands.removeElementAt(i);
            islands.addSFSObject(island);
        }
        user.putSFSArray("islands", islands);
        user_.putSFSObject("player_object", user);
        save_user_data(user_id, user_);

        SFSObject resp = new SFSObject();
        resp.putBool("success", true);

        //SFSObject resp = (SFSObject) SFSObject.newFromJsonData("{\"user_monster_id\":"+id+",\"properties\":[],\"success\":true}");

        return resp; //{"success":true}
    }

    static SFSObject[] mute_monster(String user_id, SFSObject input) {
        long id = input.getLong("user_monster_id");
        int isMuted = 0;

        SFSObject user_ = load_user_data(user_id);
        SFSObject user = (SFSObject) user_.getSFSObject("player_object");
        SFSArray islands = (SFSArray) user.getSFSArray("islands");
        for (int i = 0; i < islands.size();i++) {
            SFSObject island = (SFSObject) islands.getSFSObject(i);
            SFSArray monsters = (SFSArray) island.getSFSArray("monsters");
            for (int u = 0; u < monsters.size();u++) {
                SFSObject structure = (SFSObject) monsters.getSFSObject(u);

                Long structure_id = structure.getLong("user_monster_id");

                if (structure_id == id) {
                    isMuted = (structure.getInt("muted")==0)?1:0;
                    structure.putInt("muted", isMuted);
                }
                monsters.removeElementAt(u);
                monsters.addSFSObject(structure);
            }
            island.putSFSArray("monsters", monsters);
            islands.removeElementAt(i);
            islands.addSFSObject(island);
        }
        user.putSFSArray("islands", islands);
        user_.putSFSObject("player_object", user);
        save_user_data(user_id, user_);

        SFSObject resp = new SFSObject();
        resp.putBool("success", true);
        resp.putInt("muted", isMuted);
        resp.putLong("user_monster_id", id);

        SFSObject resp2 = new SFSObject();
        resp2.putInt("muted", isMuted);
        resp2.putLong("user_monster_id", id);

        SFSObject[] resps =  new SFSObject[]{resp,resp2};

        //SFSObject resp = (SFSObject) SFSObject.newFromJsonData("{\"user_monster_id\":"+id+",\"properties\":[],\"success\":true}");

        return resps; //{"success":true}
    }

    static SFSObject[] scale_monster(String user_id, SFSObject input) {
        long id = input.getLong("user_monster_id");
        boolean on = input.containsKey("mega_enable")?input.getBool("mega_enable"):true;

        SFSObject user_ = load_user_data(user_id);
        SFSObject user = (SFSObject) user_.getSFSObject("player_object");
        SFSArray islands = (SFSArray) user.getSFSArray("islands");
        for (int i = 0; i < islands.size();i++) {
            SFSObject island = (SFSObject) islands.getSFSObject(i);
            SFSArray monsters = (SFSArray) island.getSFSArray("monsters");
            for (int u = 0; u < monsters.size();u++) {
                SFSObject structure = (SFSObject) monsters.getSFSObject(u);

                Long structure_id = structure.getLong("user_monster_id");

                if (structure_id == id) {
                    structure.putSFSObject("megamonster", SFSObject.newFromJsonData("{\"permamega\":true,\"currently_mega\":"+(on?"true":"false")+"}"));
                }
                monsters.removeElementAt(u);
                monsters.addSFSObject(structure);
            }
            island.putSFSArray("monsters", monsters);
            islands.removeElementAt(i);
            islands.addSFSObject(island);
        }
        user.putSFSArray("islands", islands);
        user_.putSFSObject("player_object", user);
        save_user_data(user_id, user_);

        SFSObject resp = new SFSObject();
        resp.putBool("success", true);
        resp.putLong("user_monster_id", id);

        SFSObject resp2 = new SFSObject();
        resp2.putSFSObject("megamonster", SFSObject.newFromJsonData("{\"permamega\":true,\"currently_mega\":"+(on?"true":"false")+"}"));
        resp2.putLong("user_monster_id", id);
        resp2.putSFSArray("properties",  new SFSArray());

        SFSObject[] resps =  new SFSObject[]{resp,resp2};

        //SFSObject resp = (SFSObject) SFSObject.newFromJsonData("{\"user_monster_id\":"+id+",\"properties\":[],\"success\":true}");

        return resps; //{"success":true}
    }

    // {"monster_id":332,"nursery_id":207338,"quest_claim_id":0,"starpower_purchase":false}
    public static SFSObject buy_egg(String user_id, SFSObject input) {
        int monster_id = input.getInt("monster_id");
        long nursery_id = input.getLong("nursery_id");

        if (nursery_id == 0)  {
            SFSObject error = new SFSObject();
            error.putUtfString("message", "Please use nursery for buying eggs!");
            error.putUtfString("cmd", "gs_buy_egg");
            error.putBool("success", false);
            return error;
        }

        SFSObject user_ = load_user_data(user_id);
        SFSObject user = (SFSObject) user_.getSFSObject("player_object");
        SFSArray islands = (SFSArray) user.getSFSArray("islands");
        long active = user.getLong("active_island");

        SFSObject egg  = new SFSObject();
        for (int i = 0; i < islands.size();i++) {
            SFSObject island = (SFSObject) islands.getSFSObject(i);

            if (island.getLong("user_island_id") == active) {
                SFSArray eggs = (SFSArray) island.getSFSArray("eggs");


                egg.putInt("monster", monster_id);
                egg.putLong("laid_on", -1);
                egg.putLong("hatches_on", -1);
                egg.putLong("structure", nursery_id);
                egg.putInt("user_egg_id", eggs.size());

                eggs.addSFSObject(egg);

                island.putSFSArray("eggs", eggs);
            } else {
                continue;
            }
            islands.removeElementAt(i);
            islands.addSFSObject(island);
            break;
        }
        user.putSFSArray("islands", islands);
        user_.putSFSObject("player_object", user);
        save_user_data(user_id, user_);

        SFSObject resp = new SFSObject();
        resp.putSFSArray("properties",  new SFSArray());
        resp.putSFSObject("user_egg", egg);
        resp.putBool("success", true);
        resp.putBool("remove_buyback", false);

        //SFSObject resp = (SFSObject) SFSObject.newFromJsonData("{\"user_monster_id\":"+id+",\"properties\":[],\"success\":true}");

        return resp;
    }

    //{"user_monster_id":977020}
    public static SFSObject[] collect_monster(String user_id, SFSObject input) {
        long monster_id = input.getLong("user_monster_id");

        SFSObject resp = new SFSObject();
        resp.putLong("user_monster_id", monster_id);
        resp.putInt("coins",  0);
        resp.putBool("success", true);

        SFSObject resp2 = new SFSObject();
        resp2.putSFSArray("properties",  new SFSArray());
        resp2.putInt("collected_coins", 0);
        resp2.putLong("last_collection", System.currentTimeMillis()+10000000);
        resp2.putLong("user_monster_id", monster_id);

        SFSObject[] resps =  new SFSObject[]{resp,resp2};
        return resps;
    }

    // gs_hatch_egg {"pos_y":19,"pos_x":20,"user_egg_id":0,"store_in_hotel":false,"costume":0,"flip":0}
    public static SFSObject hatch_egg(String user_id, SFSObject input) {
        int x = input.getInt("pos_x");
        int y = input.getInt("pos_y");
        int f = input.getInt("flip");
        long id = input.getLong("user_egg_id");

        SFSObject user_ = load_user_data(user_id);
        SFSObject user = (SFSObject) user_.getSFSObject("player_object");
        SFSArray islands = (SFSArray) user.getSFSArray("islands");
        long type = 0;
        boolean isN  = false;

        long active = user.getLong("active_island");

        Random rnd = new Random(System.currentTimeMillis());
        int nid = 10000 + rnd.nextInt(999999 - 10000 + 1);

        SFSObject monster  = new SFSObject();
        for (int i = 0; i < islands.size();i++) {
            SFSObject island = (SFSObject) islands.getSFSObject(i);

            if (island.getLong("user_island_id") == active) {
                SFSArray eggs = (SFSArray) island.getSFSArray("eggs");
                SFSArray monsters = (SFSArray) island.getSFSArray("monsters");

                for (int u = 0; u < eggs.size();u++) {
                    SFSObject egg = (SFSObject) eggs.getSFSObject(u);
                    if (egg.getInt("user_egg_id") == id) {
                        eggs.removeElementAt(u);
                        isN=true;
                        type=egg.getInt("monster");
                    }
                }

                island.putSFSArray("eggs", new SFSArray());

                if (!isN) {
                    type=id;
                }

                monster.putDouble("volume", 1.0d);
                monster.putInt("pos_x", x);
                monster.putInt("pos_y", y);
                monster.putInt("flip", f);
                monster.putInt("level", 21);
                monster.putInt("happiness", 100);
                monster.putUtfString("collection_type", "coins");
                monster.putUtfString("name", "Made by @msm_hacks");
                monster.putSFSObject("megamonster", SFSObject.newFromJsonData("{\"permamega\":true,\"currently_mega\":false}"));
                monster.putInt("in_hotel", 0);
                monster.putInt("muted", 0);
                monster.putLong("user_monster_id", nid);
                monster.putLong("monster", type);
                monster.putLong("last_collection", System.currentTimeMillis()+10000000);
                monster.putLong("island", active);
                monsters.addSFSObject(monster);

                island.putSFSArray("monsters", monsters);
            } else {
                continue;
            }
            islands.removeElementAt(i);
            islands.addSFSObject(island);
            break;
        }
        user.putSFSArray("islands", islands);
        user_.putSFSObject("player_object", user);
        save_user_data(user_id, user_);

        SFSObject resp = new SFSObject();
        resp.putSFSArray("properties",  new SFSArray());
        resp.putSFSObject("monster", monster);
        resp.putLong("user_egg_id", id);
        resp.putLong("island", active);
        resp.putBool("success", true);
        resp.putBool("create_in_storage", false);
        resp.putBool("directPlace", !isN);

        //SFSObject resp = (SFSObject) SFSObject.newFromJsonData("{\"user_monster_id\":"+id+",\"properties\":[],\"success\":true}");

        return resp;
    }

    //{
    //      "island":1030368419,
    //      "monster":{},
    //      "directPlace":false,
    //      "properties":[],
    //      "success":true,
    //      "create_in_storage":false,
    //      "user_egg_id":1
    // }

    //{
    //      "island":141762241,
    //      "monster":{},
    //      "directPlace":true,
    //      "properties":[],
    //      "success":true,
    //      "create_in_storage":false,
    //      "user_egg_id":140
    // }

    //{
    //       "island":194291744,
    //       "monster":{},
    //       "directPlace":true,
    //       "properties":[],
    //       "success":true,
    //       "create_in_storage":false,
    //       "user_egg_id":130
    // }
}