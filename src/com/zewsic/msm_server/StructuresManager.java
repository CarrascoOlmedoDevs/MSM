package com.zewsic.msm_server;

import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.util.JSONUtil;

import java.util.Random;

import static com.zewsic.msm_server.UsersManager.load_user_data;
import static com.zewsic.msm_server.UsersManager.save_user_data;

public class StructuresManager {

    //{"pos_y":19,"user_structure_id":64,"pos_x":18,"scale":1.0}
    static SFSObject[] move_structure(String user_id, SFSObject input) {
        int x = input.getInt("pos_x");
        int y = input.getInt("pos_y");
        double s = input.getDouble("scale");
        long id = input.getLong("user_structure_id");

        SFSObject user_ = load_user_data(user_id);
        SFSObject user = (SFSObject) user_.getSFSObject("player_object");
        SFSArray islands = (SFSArray) user.getSFSArray("islands");
        for (int i = 0; i < islands.size();i++) {
            SFSObject island = (SFSObject) islands.getSFSObject(i);
            SFSArray structures = (SFSArray) island.getSFSArray("structures");
            for (int u = 0; u < structures.size();u++) {
                SFSObject structure = (SFSObject) structures.getSFSObject(u);

                Long structure_id = structure.getLong("user_structure_id");
                if (structure_id == id) {
                    structure.putInt("pos_x", x);
                    structure.putInt("pos_y", y);
                    structure.putDouble("scale", s);
                }
                structures.removeElementAt(u);
                structures.addSFSObject(structure);
            }
            island.putSFSArray("structures", structures);
            islands.removeElementAt(i);
            islands.addSFSObject(island);
        }
        user.putSFSArray("islands", islands);
        user_.putSFSObject("player_object", user);
        save_user_data(user_id, user_);

        SFSObject resp = new SFSObject();
        resp.putBool("success", true);

        SFSObject resp2 = new SFSObject();
        SFSArray resp_arr = new SFSArray();

        SFSObject resp_obj = new SFSObject();
        resp_obj.putInt("pos_x",x);
        resp_arr.addSFSObject(resp_obj);

        resp_obj = new SFSObject();
        resp_obj.putInt("pos_y",y);
        resp_arr.addSFSObject(resp_obj);


        resp2.putSFSArray("properties", resp_arr);
        resp2.putLong("user_structure_id", id);

        SFSObject[] resps =  new SFSObject[]{resp,resp2};

        return resps; //{"success":true}  gs_update_structure {"properties":[{"pos_x":18},{"pos_y":19}],"user_structure_id":64}
    }
    static SFSObject[] wtf_structure(String user_id, SFSObject input) {
        long id = input.getLong("user_structure_id");

        SFSObject user_ = load_user_data(user_id);
        SFSObject user = (SFSObject) user_.getSFSObject("player_object");
        SFSArray islands = (SFSArray) user.getSFSArray("islands");
        for (int i = 0; i < islands.size();i++) {
            SFSObject island = (SFSObject) islands.getSFSObject(i);
            SFSArray structures = (SFSArray) island.getSFSArray("structures");
            for (int u = 0; u < structures.size();u++) {
                SFSObject structure = (SFSObject) structures.getSFSObject(u);

                Long structure_id = structure.getLong("user_structure_id");
                if (structure_id == id) {
                    structure.putDouble("scale", 5);
                }
                structures.removeElementAt(u);
                structures.addSFSObject(structure);
            }
            island.putSFSArray("structures", structures);
            islands.removeElementAt(i);
            islands.addSFSObject(island);
        }
        user.putSFSArray("islands", islands);
        user_.putSFSObject("player_object", user);
        save_user_data(user_id, user_);

        SFSObject resp = new SFSObject();
        resp.putBool("success", true);

        SFSObject resp2 = new SFSObject();
        SFSArray resp_arr = new SFSArray();

        SFSObject resp_obj = new SFSObject();
        resp_obj.putInt("scale",5);
        resp_arr.addSFSObject(resp_obj);

        resp2.putSFSArray("properties", resp_arr);
        resp2.putLong("user_structure_id", id);

        SFSObject[] resps =  new SFSObject[]{resp,resp2};

        return resps; //{"success":true}  gs_update_structure {"properties":[{"pos_x":18},{"pos_y":19}],"user_structure_id":64}
    }
    static SFSObject[] flip_structure(String user_id, SFSObject input) {
        boolean flip = input.getBool("flipped");
        long id = input.getLong("user_structure_id");

        SFSObject user_ = load_user_data(user_id);
        SFSObject user = (SFSObject) user_.getSFSObject("player_object");
        SFSArray islands = (SFSArray) user.getSFSArray("islands");
        for (int i = 0; i < islands.size();i++) {
            SFSObject island = (SFSObject) islands.getSFSObject(i);
            SFSArray structures = (SFSArray) island.getSFSArray("structures");
            for (int u = 0; u < structures.size();u++) {
                SFSObject structure = (SFSObject) structures.getSFSObject(u);

                Long structure_id = structure.getLong("user_structure_id");
                if (structure_id == id) {
                    structure.putInt("flip", flip?1:0);
                }
                structures.removeElementAt(u);
                structures.addSFSObject(structure);
            }
            island.putSFSArray("structures", structures);
            islands.removeElementAt(i);
            islands.addSFSObject(island);
        }
        user.putSFSArray("islands", islands);
        user_.putSFSObject("player_object", user);
        save_user_data(user_id, user_);

        SFSObject resp = new SFSObject();
        resp.putBool("success", true);

        SFSObject resp2 = new SFSObject();
        SFSArray resp_arr = new SFSArray();

        SFSObject resp_obj = new SFSObject();
        resp_obj.putInt("flip",flip?1:0);

        resp2.putSFSArray("properties", resp_arr);
        resp2.putLong("user_structure_id", id);

        SFSObject[] resps =  new SFSObject[]{resp,resp2};

        return resps; //{"success":true}  gs_update_structure {"properties":[{"pos_x":18},{"pos_y":19}],"user_structure_id":64}
    }

    static SFSObject sell_structure(String user_id, SFSObject input) {
        long id = input.getLong("user_structure_id");

        SFSObject user_ = load_user_data(user_id);
        SFSObject user = (SFSObject) user_.getSFSObject("player_object");
        SFSArray islands = (SFSArray) user.getSFSArray("islands");
        for (int i = 0; i < islands.size();i++) {
            SFSObject island = (SFSObject) islands.getSFSObject(i);
            SFSArray structures = (SFSArray) island.getSFSArray("structures");
            for (int u = 0; u < structures.size();u++) {
                SFSObject structure = (SFSObject) structures.getSFSObject(u);

                Long structure_id = structure.getLong("user_structure_id");

                structures.removeElementAt(u);
                if (structure_id != id) {
                    structures.addSFSObject(structure);
                }


            }
            island.putSFSArray("structures", structures);
            islands.removeElementAt(i);
            islands.addSFSObject(island);
        }
        user.putSFSArray("islands", islands);
        user_.putSFSObject("player_object", user);
        save_user_data(user_id, user_);

        SFSObject resp = new SFSObject();
        resp.putBool("success", true);
        resp.putSFSArray("properties", new SFSArray());
        resp.putLong("user_structure_id", id);

        return resp; //{"success":true}  gs_update_structure {"properties":[{"pos_x":18},{"pos_y":19}],"user_structure_id":64}
    }

    // {"pos_y":21,"pos_x":25,"quest_claim_id":0,"scale":1.0,"starpower_purchase":false,"structure_id":2,"flip":0}
    static SFSObject buy_structure(String user_id, SFSObject input) {
        int x = input.getInt("pos_x");
        int y = input.getInt("pos_y");
        double s = input.getDouble("scale");
        int id = input.getInt("structure_id");
        int flip = input.getInt("flip");

        Random rnd = new Random(System.currentTimeMillis());
        int nid = 10000 + rnd.nextInt(999999 - 10000 + 1);

        SFSObject structure = new SFSObject();

        SFSObject user_ = load_user_data(user_id);
        SFSObject user = (SFSObject) user_.getSFSObject("player_object");
        long active = user.getLong("active_island");
        SFSArray islands = (SFSArray) user.getSFSArray("islands");
        for (int i = 0; i < islands.size();i++) {
            SFSObject island = (SFSObject) islands.getSFSObject(i);
            if (island.getLong("user_island_id") == active) {

                SFSArray structures = (SFSArray) island.getSFSArray("structures");


                structure.putLong("island", active);
                structure.putInt("book_value", 100);
                structure.putInt("pos_x", x);
                structure.putInt("pos_y", y);
                structure.putInt("flip", flip);
                structure.putInt("muted", 0);
                structure.putInt("in_warehouse", 0);
                structure.putInt("is_upgrading", 0);
                structure.putInt("is_complete", 1);
                structure.putLong("user_structure_id", nid);
                structure.putLong("last_collection", System.currentTimeMillis());
                structure.putInt("structure", id);
                structure.putDouble("scale", s);

                structures.addSFSObject(structure);

                island.putSFSArray("structures", structures);
                islands.removeElementAt(i);
                islands.addSFSObject(island);
            }
        }
        user.putSFSArray("islands", islands);
        user_.putSFSObject("player_object", user);
        save_user_data(user_id, user_);

        SFSObject resp = new SFSObject();
        resp.putBool("success", true);
        resp.putSFSObject("user_structure", structure);
        resp.putSFSArray("monster_happy_effects", new SFSArray());
        resp.putSFSArray("properties", new SFSArray());

        return resp; //{"success":true}  gs_update_structure {"properties":[{"pos_x":18},{"pos_y":19}],"user_structure_id":64}
    }
}


/*
{"user_structure":{},
"properties":[],
"success":true,
"monster_happy_effects":[]}
 */