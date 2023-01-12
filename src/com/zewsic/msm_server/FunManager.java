package com.zewsic.msm_server;

import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.Random;

import static com.zewsic.msm_server.UsersManager.update_user_long;

public class FunManager {
    static SFSObject play_scratch(String user_id, SFSObject input) {
        String type = input.getUtfString("type");
        if (type.contains("S")) {
            String[] prizes = new String[]{"coins", "diamonds", "food", "relics", "keys"};
            SFSObject resp = new SFSObject();
            SFSObject ticket = new SFSObject();

            Random rnd = new Random(System.currentTimeMillis());
            int nid0 = rnd.nextInt(1);
            int nid1 = 1 + rnd.nextInt(999999 + 1);
            ticket.putInt("amount", nid1);

            int nid2 = 1 + rnd.nextInt(999999 + 1);
            ticket.putInt("scaled_amount", nid2);

            int nid3 = rnd.nextInt(4);
            ticket.putUtfString("prize", prizes[nid3]);

            int nid4 = rnd.nextInt(11);
            ticket.putInt("id", nid0==0?101:nid4);

            ticket.putUtfString("type", "S");
            ticket.putInt("is_top_prize", nid0);

            resp.putBool("success", true);
            resp.putSFSObject("ticket", ticket);

            SFSArray prizes_list = new SFSArray();
            for (int y = 0; y < 12; y++) {
                ticket = new SFSObject();
                if (y == nid4) {
                    ticket.putInt("amount", nid1);
                    ticket.putInt("scaled_amount", nid2);
                    ticket.putUtfString("prize", prizes[nid3]);
                    ticket.putInt("id", nid4);
                } else {
                    nid1 = 1 + rnd.nextInt(99999999 + 1);
                    ticket.putInt("amount", nid1);

                    nid2 = 1 + rnd.nextInt(99999999 + 1);
                    ticket.putInt("scaled_amount", nid2);
                    ticket.putInt("is_top_prize", 1);

                    nid3 = rnd.nextInt(4);
                    ticket.putUtfString("prize", prizes[nid3]);

                    ticket.putInt("id", y);
                }
                prizes_list.addSFSObject(ticket);
            }
            SFSObject qq = new SFSObject();
            qq.putSFSArray("prizes", prizes_list);
            resp.putSFSObject("scaled_prizes", qq);
            return resp;
        } else {
            SFSObject resp = new SFSObject();
            SFSObject ticket = new SFSObject();

            Random rnd = new Random(System.currentTimeMillis());

            ticket.putInt("amount", 1+rnd.nextInt(30));
            ticket.putUtfString("prize", "monster");
            ticket.putInt("id", 17);
            ticket.putUtfString("type", "M");
            ticket.putInt("is_top_prize",1);

            resp.putBool("success", true);
            resp.putSFSObject("ticket", ticket);
            return resp;
        }
    }
}

//TRANSPARENT REQUEST: gs_play_scratch_off
//{ServerExtension}: {"type":"M"}
// TRANSPARENT RESPONSE: gs_play_scratch_off
//{"ticket":{"amount":5,"id":17,"type":"M","prize":"monster"},"success":true}


//TRANSPARENT REQUEST: gs_purchase_scratch_off
//{"type":"M","requestFree":false}
//TRANSPARENT RESPONSE: gs_play_scratch_off
//{"ticket":{"amount":5,"is_top_prize":0,"id":17,"type":"M","prize":"monster"},"success":true,"properties":[]}






//TRANSPARENT REQUEST: gs_play_scratch_off
//{"type":"S"}
//TRANSPARENT RESPONSE: gs_play_scratch_off
//{"ticket":{"amount":250,"is_top_prize":0,"id":11,"type":"S","prize":"food"}, "success":true,"scaled_prizes":{
//      "prizes":[
//          {"amount":10000,"id":2,"scaled_amount":290000,"prize":"coins"},
//          {"amount":2500,"id":9,"scaled_amount":72500,"prize":"coins"},
//          {"amount":1000,"id":4,"scaled_amount":29000,"prize":"coins"},
//          {"amount":500,"id":6,"scaled_amount":14500,"prize":"coins"},
//          {"amount":5000,"id":5,"scaled_amount":36000,"prize":"food"},
//          {"amount":2500,"id":7,"scaled_amount":18000,"prize":"food"},
//          {"amount":500,"id":3,"scaled_amount":3600,"prize":"food"},
//          {"amount":250,"id":11,"scaled_amount":1800,"prize":"food"}
//          ]
//      }
// }


//11:42:14,147 INFO  [Thread-125] Extensions     - {ServerExtension}: TRANSPARENT REQUEST: gs_collect_scratch_off
//11:42:14,147 INFO  [Thread-125] Extensions     - {ServerExtension}: {"type":"S","structure":0}
//11:42:14,308 INFO  [New I/O client worker #4-1] Extensions     - {ServerExtension}: TRANSPARENT RESPONSE: gs_collect_scratch_off
//11:42:14,308 INFO  [New I/O client worker #4-1] Extensions     - {ServerExtension}: {"success":true,"rare":false,"epic":false,"properties":[{"coins":43016582},{"diamonds":107},{"ethereal_currency":10026},{"starpower":886},{"keys":176},{"food":355690},{"xp":49254438},{"level":40},{"daily_bonus_type":"none"},{"daily_bonus_amount":0},{"relics":91},{"has_free_ad_scratch":true},{"daily_relic_purchase_count":0},{"relic_diamond_cost":1},{"next_relic_reset":1660521600000},{"premium":1},{"earned_starpower":712},{"speed_up_credit":8},{"battle_xp":625},{"battle_level":8},{"medals":491}]}