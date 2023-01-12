package com.zewsic.msm_server;

import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.Random;

import static com.zewsic.msm_server.UsersManager.*;

public class IslandsManager {
    static SFSObject change_island(String user_id, SFSObject input) {
        long island_id = input.getLong("user_island_id");

        update_user_long(user_id, "active_island", island_id);

        SFSObject resp = new SFSObject();
        resp.putBool("success", true);
        resp.putLong("user_island_id", island_id);
        return resp;
    }

    //{"user_island_id":1000,"warp_speed":1.7416666746139526}
    static SFSObject set_warp_island(String user_id, SFSObject input) {
        long island_id = input.getLong("user_island_id");
        double warp = input.getDouble("warp_speed");

        SFSObject user_ = load_user_data(user_id);
        SFSObject user = (SFSObject) user_.getSFSObject("player_object");
        SFSArray islands = (SFSArray) user.getSFSArray("islands");
        for (int i = 0; i < islands.size();i++) {
            SFSObject island = (SFSObject) islands.getSFSObject(i);
            if (island.getLong("user_island_id") == island_id) {
                island.putDouble("warp_speed",warp);
            }
        }

        user.putSFSArray("islands", islands);
        user_.putSFSObject("player_object", user);
        save_user_data(user_id, user_);

        SFSObject resp = new SFSObject();
        resp.putBool("success", true);
        return resp;
    }

    // gs_buy_island {"island_id":2,"island_name":"","starpower_purchase":false}
    static SFSObject buy_island(String user_id, SFSObject input) {
        int island_id = input.getInt("island_id");

        SFSObject user_ = load_user_data(user_id);
        SFSObject user = (SFSObject) user_.getSFSObject("player_object");
        SFSArray islands = (SFSArray) user.getSFSArray("islands");

        for (int i = 0; i < islands.size();i++) {
            SFSObject _island = (SFSObject) islands.getSFSObject(i);
            int iid = _island.getInt("island");
            if (island_id == iid) {
                SFSObject error = new SFSObject();
                error.putUtfString("message", "Island already bought!");
                error.putUtfString("cmd", "gs_buy_island");
                error.putBool("success", false);
                return error;
            }
        }

        SFSObject island = new SFSObject();
        island.putSFSArray("eggs", new SFSArray());
        island.putDouble("warp_speed", 1.0d);
        island.putInt("island", island_id);
        island.putSFSArray("structures", new SFSArray());
        island.putSFSArray("monsters", new SFSArray());
        island.putLong("dislikes", 0);
        island.putLong("likes", 0);
        island.putSFSArray("fuzer", new SFSArray());
        island.putSFSArray("baking", new SFSArray());
        island.putSFSArray("costumes_owned", new SFSArray());
        island.putSFSArray("breeding", new SFSArray());
        island.putSFSArray("torches", new SFSArray());
        island.putInt("last_player_level", 75);
        island.putInt("num_torches", 0);
        island.putLong("user_island_id", 1000 + island_id);
        island.putLong("user", 100000000);
        island.putInt("type", island_id);

        if (island_id == 9){
            island.putSFSArray("tribal_requests", new SFSArray());
            island.putSFSArray("tribal_quests", new SFSArray());

            SFSObject tid = new SFSObject();
            tid.putUtfString("chief_name", "@msm_hacks");
            tid.putUtfString("name", "@msm_hacks");
            tid.putLong("user_island_id", 1000 + island_id);
            tid.putLong("chief", 100000000);
            tid.putInt("members", 1);
            tid.putInt("monsters", 999);
            tid.putInt("rank", 999999);

            island.putSFSObject("tribal_island_data", tid);
        }

        islands.addSFSObject(island);

        user.putSFSArray("islands", islands);
        user_.putSFSObject("player_object", user);
        save_user_data(user_id, user_);

        SFSObject resp = new SFSObject();
        resp.putBool("success", true);
        resp.putSFSArray("properties", new SFSArray());
        resp.putSFSArray("tracks", new SFSArray());
        resp.putSFSObject("user_island", island);
        return resp;

    }
}


/*
{"tracks":[],"properties":[
    {"coins":8309},{"diamonds":3},{"ethereal_currency":0},
    {"starpower":0},{"keys":3},{"food":10},{"xp":150},{"level":5},{"daily_bonus_type":"none"},
    {"daily_bonus_amount":0},{"relics":0},{"has_free_ad_scratch":true},{"daily_relic_purchase_count":0},
    {"relic_diamond_cost":1},{"next_relic_reset":1658361600000},{"premium":0},{"earned_starpower":0},
    {"speed_up_credit":8},{"battle_xp":0},{"battle_level":1},{"medals":0}
], "user_island":{"island":2,"date_created":1658344590000,"likes":0,"eggs":
[{"monster":1,"island":1061085891,"costume":{"eq":0,"p":[]},
"laid_on":1658344590025,"hatches_on":1658344590025,"structure":2,
"user_egg_id":1}],"user":1007754078,"monsters":[],
"user_island_id":1061085891,"fuzer":[],
"warp_speed":1.0,"last_player_level":-1,"breeding":[],
"structures":[{"island":1061085891,"date_created":1658344590009,
"last_collection":1658344590009,"pos_x":29,"flip":0,"muted":0,"in_warehouse":0,
"pos_y":9,"is_upgrading":0,"is_complete":1,"user_structure_id":1,"structure":12,
"scale":1.0,"building_completed":1658344590009},{"island":1061085891,"date_created":1658344590009,
"last_collection":1658344590009,"pos_x":35,"flip":0,"muted":0,"in_warehouse":0,"pos_y":17,"is_upgrading":0,
"is_complete":1,"user_structure_id":2,"structure":1,"scale":1.0,"building_completed":1658344590009},
{"island":1061085891,"date_created":1658344590009,"last_collection":1658344590009,"pos_x":21,
"flip":0,"muted":0,"in_warehouse":0,"pos_y":3,"is_upgrading":0,"is_complete":1,"user_structure_id":3,
"structure":2,"scale":1.0,"building_completed":1658344590009},{"island":1061085891,"pos_x":14,"flip":0,
"muted":0,"in_warehouse":0,"pos_y":12,"is_upgrading":0,"is_complete":1,"user_structure_id":4,"structure":112,
"scale":1.0},{"island":1061085891,"pos_x":5,"flip":0,"muted":0,"in_warehouse":0,"pos_y":26,"is_upgrading":0,
"is_complete":1,"user_structure_id":5,"structure":117,"scale":1.0},{"island":1061085891,"pos_x":9,"flip":0,
"muted":0,"in_warehouse":0,"pos_y":21,"is_upgrading":0,"is_complete":1,"user_structure_id":6,"structure":115,
"scale":1.0},{"island":1061085891,"pos_x":22,"flip":0,"muted":0,"in_warehouse":0,"pos_y":32,"is_upgrading":0,
"is_complete":1,"user_structure_id":7,"structure":116,"scale":1.0},{"island":1061085891,"pos_x":7,"flip":0,"muted":0,
"in_warehouse":0,"pos_y":17,"is_upgrading":0,"is_complete":1,"user_structure_id":8,"structure":113,"scale":1.0},
{"island":1061085891,"pos_x":6,"flip":0,"muted":0,"in_warehouse":0,"pos_y":14,"is_upgrading":0,"is_complete":1,"user_structure_id":9,
"structure":114,"scale":1.0},{"island":1061085891,"pos_x":16,"flip":0,"muted":0,"in_warehouse":0,"pos_y":27,"is_upgrading":0,"is_complete":1,
"user_structure_id":10,"structure":116,"scale":1.0},{"island":1061085891,"pos_x":5,"flip":0,"muted":0,"in_warehouse":0,"pos_y":23,"is_upgrading":0,
"is_complete":1,"user_structure_id":11,"structure":115,"scale":1.0},{"island":1061085891,"pos_x":7,"flip":0,"muted":0,
"in_warehouse":0,"pos_y":27,"is_upgrading":0,"is_complete":1,"user_structure_id":12,"structure":112,"scale":1.0},
{"island":1061085891,"pos_x":11,"flip":0,"muted":0,"in_warehouse":0,"pos_y":9,"is_upgrading":0,"is_complete":1,"user_structure_id":13,"structure":117,"scale":1.0},{"island":1061085891,"pos_x":13,"flip":0,"muted":0,"in_warehouse":0,"pos_y":21,"is_upgrading":0,"is_complete":1,"user_structure_id":14,"structure":112,"scale":1.0},{"island":1061085891,"pos_x":10,"flip":0,"muted":0,"in_warehouse":0,"pos_y":28,"is_upgrading":0,"is_complete":1,"user_structure_id":15,"structure":114,"scale":1.0},{"island":1061085891,"pos_x":15,"flip":0,"muted":0,"in_warehouse":0,"pos_y":17,"is_upgrading":0,"is_complete":1,"user_structure_id":17,"structure":115,"scale":1.0},{"island":1061085891,"pos_x":20,"flip":0,"muted":0,"in_warehouse":0,"pos_y":35,"is_upgrading":0,"is_complete":1,"user_structure_id":16,"structure":113,"scale":1.0},{"island":1061085891,"pos_x":16,"flip":0,"muted":0,"in_warehouse":0,"pos_y":22,"is_upgrading":0,"is_complete":1,"user_structure_id":19,"structure":113,"scale":1.0},{"island":1061085891,"pos_x":12,"flip":0,"muted":0,"in_warehouse":0,"pos_y":31,"is_upgrading":0,"is_complete":1,"user_structure_id":18,"structure":112,"scale":1.0},{"island":1061085891,"pos_x":11,"flip":0,"muted":0,"in_warehouse":0,"pos_y":17,"is_upgrading":0,"is_complete":1,"user_structure_id":21,"structure":116,"scale":1.0},{"island":1061085891,"pos_x":22,"flip":0,"muted":0,"in_warehouse":0,"pos_y":26,"is_upgrading":0,"is_complete":1,"user_structure_id":20,"structure":115,"scale":1.0},{"island":1061085891,"pos_x":24,"flip":0,"muted":0,"in_warehouse":0,"pos_y":33,"is_upgrading":0,"is_complete":1,"user_structure_id":23,"structure":112,"scale":1.0},{"island":1061085891,"pos_x":26,"flip":0,"muted":0,"in_warehouse":0,"pos_y":31,"is_upgrading":0,"is_complete":1,"user_structure_id":22,"structure":117,"scale":1.0},{"island":1061085891,"pos_x":23,"flip":0,"muted":0,"in_warehouse":0,"pos_y":28,"is_upgrading":0,"is_complete":1,"user_structure_id":25,"structure":114,"scale":1.0},{"island":1061085891,"pos_x":18,"flip":0,"muted":0,"in_warehouse":0,"pos_y":29,"is_upgrading":0,"is_complete":1,"user_structure_id":24,"structure":113,"scale":1.0},{"island":1061085891,"pos_x":16,"flip":0,"muted":0,"in_warehouse":0,"pos_y":34,"is_upgrading":0,"is_complete":1,"user_structure_id":27,"structure":112,"scale":1.0},{"island":1061085891,"pos_x":3,"flip":0,"muted":0,"in_warehouse":0,"pos_y":20,"is_upgrading":0,"is_complete":1,"user_structure_id":26,"structure":115,"scale":1.0},{"island":1061085891,"pos_x":8,"flip":0,"muted":0,"in_warehouse":0,"pos_y":32,"is_upgrading":0,"is_complete":1,"user_structure_id":29,"structure":115,"scale":1.0},{"island":1061085891,"pos_x":2,"flip":0,"muted":0,"in_warehouse":0,"pos_y":23,"is_upgrading":0,"is_complete":1,"user_structure_id":28,"structure":113,"scale":1.0},{"island":1061085891,"pos_x":28,"flip":0,"muted":0,"in_warehouse":0,"pos_y":22,"is_upgrading":0,"is_complete":1,"user_structure_id":31,"structure":112,"scale":1.0},{"island":1061085891,"pos_x":14,"flip":0,"muted":0,"in_warehouse":0,"pos_y":33,"is_upgrading":0,"is_complete":1,"user_structure_id":30,"structure":116,"scale":1.0},{"island":1061085891,"pos_x":13,"flip":0,"muted":0,"in_warehouse":0,"pos_y":23,"is_upgrading":0,"is_complete":1,"user_structure_id":34,"structure":115,"scale":1.0},{"island":1061085891,"pos_x":2,"flip":0,"muted":0,"in_warehouse":0,"pos_y":18,"is_upgrading":0,"is_complete":1,"user_structure_id":35,"structure":117,"scale":1.0},{"island":1061085891,"pos_x":6,"flip":0,"muted":0,"in_warehouse":0,"pos_y":31,"is_upgrading":0,"is_complete":1,"user_structure_id":32,"structure":113,"scale":1.0},{"island":1061085891,"pos_x":16,"flip":0,"muted":0,"in_warehouse":0,"pos_y":10,"is_upgrading":0,"is_complete":1,"user_structure_id":33,"structure":114,"scale":1.0},{"island":1061085891,"pos_x":11,"flip":0,"muted":0,"in_warehouse":0,"pos_y":13,"is_upgrading":0,"is_complete":1,"user_structure_id":38,"structure":113,"scale":1.0},{"island":1061085891,"pos_x":13,"flip":0,"muted":0,"in_warehouse":0,"pos_y":29,"is_upgrading":0,"is_complete":1,"user_structure_id":39,"structure":113,"scale":1.0},{"island":1061085891,"pos_x":22,"flip":0,"muted":0,"in_warehouse":0,"pos_y":23,"is_upgrading":0,"is_complete":1,"user_structure_id":36,"structure":116,"scale":1.0},{"island":1061085891,"pos_x":19,"flip":0,"muted":0,"in_warehouse":0,"pos_y":12,"is_upgrading":0,"is_complete":1,"user_structure_id":37,"structure":112,"scale":1.0},{"island":1061085891,"pos_x":22,"flip":0,"muted":0,"in_warehouse":0,"pos_y":9,"is_upgrading":0,"is_complete":1,"user_structure_id":42,"structure":112,"scale":1.0},{"island":1061085891,"pos_x":19,"flip":0,"muted":0,"in_warehouse":0,"pos_y":32,"is_upgrading":0,"is_complete":1,"user_structure_id":43,"structure":113,"scale":1.0},{"island":1061085891,"pos_x":28,"flip":0,"muted":0,"in_warehouse":0,"pos_y":20,"is_upgrading":0,"is_complete":1,"user_structure_id":40,"structure":115,"scale":1.0},{"island":1061085891,"pos_x":29,"flip":0,"muted":0,"in_warehouse":0,"pos_y":24,"is_upgrading":0,"is_complete":1,"user_structure_id":41,"structure":113,"scale":1.0},{"island":1061085891,"pos_x":26,"flip":0,"muted":0,"in_warehouse":0,"pos_y":22,"is_upgrading":0,"is_complete":1,"user_structure_id":46,"structure":112,"scale":1.0},{"island":1061085891,"pos_x":11,"flip":0,"muted":0,"in_warehouse":0,"pos_y":20,"is_upgrading":0,"is_complete":1,"user_structure_id":47,"structure":114,"scale":1.0},{"island":1061085891,"pos_x":6,"flip":0,"muted":0,"in_warehouse":0,"pos_y":21,"is_upgrading":0,"is_complete":1,"user_structure_id":44,"structure":116,"scale":1.0},{"island":1061085891,"pos_x":14,"flip":0,"muted":0,"in_warehouse":0,"pos_y":10,"is_upgrading":0,"is_complete":1,"user_structure_id":45,"structure":112,"scale":1.0},{"island":1061085891,"pos_x":10,"flip":0,"muted":0,"in_warehouse":0,"pos_y":24,"is_upgrading":0,"is_complete":1,"user_structure_id":51,"structure":112,"scale":1.0},{"island":1061085891,"pos_x":21,"flip":0,"muted":0,"in_warehouse":0,"pos_y":25,"is_upgrading":0,"is_complete":1,"user_structure_id":50,"structure":112,"scale":1.0},{"island":1061085891,"pos_x":17,"flip":0,"muted":0,"in_warehouse":0,"pos_y":13,"is_upgrading":0,"is_complete":1,"user_structure_id":49,"structure":115,"scale":1.0},{"island":1061085891,"pos_x":12,"flip":0,"muted":0,"in_warehouse":0,"pos_y":34,"is_upgrading":0,"is_complete":1,"user_structure_id":48,"structure":112,"scale":1.0},{"island":1061085891,"pos_x":15,"flip":0,"muted":0,"in_warehouse":0,"pos_y":36,"is_upgrading":0,"is_complete":1,"user_structure_id":55,"structure":115,"scale":1.0},{"island":1061085891,"pos_x":1,"flip":0,"muted":0,"in_warehouse":0,"pos_y":20,"is_upgrading":0,"is_complete":1,"user_structure_id":54,"structure":112,"scale":1.0},{"island":1061085891,"pos_x":17,"flip":0,"muted":0,"in_warehouse":0,"pos_y":36,"is_upgrading":0,"is_complete":1,"user_structure_id":53,"structure":116,"scale":1.0},{"island":1061085891,"pos_x":7,"flip":0,"muted":0,"in_warehouse":0,"pos_y":9,"is_upgrading":0,"is_complete":1,"user_structure_id":52,"structure":115,"scale":1.0},{"island":1061085891,"pos_x":14,"flip":0,"muted":0,"in_warehouse":0,"pos_y":26,"is_upgrading":0,"is_complete":1,"user_structure_id":59,"structure":115,"scale":1.0},{"island":1061085891,"pos_x":25,"flip":0,"muted":0,"in_warehouse":0,"pos_y":21,"is_upgrading":0,"is_complete":1,"user_structure_id":58,"structure":113,"scale":1.0},{"island":1061085891,"pos_x":19,"flip":0,"muted":0,"in_warehouse":0,"pos_y":37,"is_upgrading":0,"is_complete":1,"user_structure_id":57,"structure":112,"scale":1.0},{"island":1061085891,"pos_x":9,"flip":0,"muted":0,"in_warehouse":0,"pos_y":9,"is_upgrading":0,"is_complete":1,"user_structure_id":56,"structure":112,"scale":1.0},{"island":1061085891,"pos_x":9,"flip":0,"muted":0,"in_warehouse":0,"pos_y":34,"is_upgrading":0,"is_complete":1,"user_structure_id":63,"structure":113,"scale":1.0},{"island":1061085891,"pos_x":10,"flip":0,"muted":0,"in_warehouse":0,"pos_y":14,"is_upgrading":0,"is_complete":1,"user_structure_id":62,"structure":115,"scale":1.0},{"island":1061085891,"pos_x":19,"flip":0,"muted":0,"in_warehouse":0,"pos_y":22,"is_upgrading":0,"is_complete":1,"user_structure_id":61,"structure":112,"scale":1.0},{"island":1061085891,"pos_x":21,"flip":0,"muted":0,"in_warehouse":0,"pos_y":29,"is_upgrading":0,"is_complete":1,"user_structure_id":60,"structure":112,"scale":1.0}],"torches":[],"dislikes":0,"baking":[],"type":2,"costumes_owned":"[]","costume_data":{"costumes":[]}},"success":true,"songs":[]}
 */