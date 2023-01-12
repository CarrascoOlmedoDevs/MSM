package com.zewsic.msm_server.fun;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.zewsic.msm_server.MainExtension;

import static com.zewsic.msm_server.UsersManager.load_user_data;

public class Screen {
    int h = 3;
    int w = 3;
    int oh = 3;
    int ow = 5;
    MainExtension self;
    User user;

    public Screen(int w, int h, int ow, int oh, MainExtension self, User user) {
        this.h=h;
        this.w=w;
        this.ow=ow;
        this.oh=oh;
        this.self=self;
        this.user=user;
    }

    int genID(int x,int y) {
        return x*100+y;
    }

    public void build_screen() {
        SFSObject user_ = load_user_data(user.getName());
        SFSObject userx = (SFSObject) user_.getSFSObject("player_object");
        long active = userx.getLong("active_island");

        int id_ = 0;

        for (int x=0;x<=w;x++) {
            for (int y=0;y<=h;y++) {
                SFSObject torch = new SFSObject();
                torch.putLong("user_torch_id", id_);
                torch.putLong("user_structure", genID(x,y));
                torch.putLong("started_at", 55);
                torch.putLong("finished_at", 999999999);
                torch.putBool("permalit", false);


                SFSObject structure = new SFSObject();

                structure.putLong("island", active);
                structure.putInt("book_value", 100);
                structure.putInt("pos_x", ow+x);
                structure.putInt("pos_y", oh+y);
                structure.putInt("flip", 0);
                structure.putInt("muted", 0);
                structure.putInt("in_warehouse", 0);
                structure.putInt("is_upgrading", 0);
                structure.putInt("is_complete", 1);
                structure.putLong("user_structure_id", genID(x,y));
                structure.putLong("user_torch_id", id_);
                structure.putSFSObject("user_torch", torch);
                structure.putLong("last_collection", System.currentTimeMillis());
                structure.putInt("structure", 206);
                structure.putDouble("scale", 1.0);

                SFSObject resp = new SFSObject();
                resp.putBool("success", true);
                resp.putSFSObject("user_structure", structure);
                resp.putSFSArray("monster_happy_effects", new SFSArray());
                resp.putSFSArray("properties", new SFSArray());
                self.send("gs_buy_structure", resp, user);

                id_ += 1;
            }
        }

        id_ = 0;

        for (int x=0;x<=w;x++) {
            for (int y=0;y<=h;y++) {
                SFSObject torch = new SFSObject();
                torch.putLong("user_torch_id", id_);
                torch.putLong("user_structure", genID(x,y));
                torch.putLong("started_at", -1);
                torch.putLong("finished_at", -1);
                torch.putBool("permalit", false);


                SFSObject resp = new SFSObject();
                resp.putBool("success", true);
                resp.putSFSObject("user_torch", torch);
                resp.putLong("island_id", active);
                resp.putSFSArray("properties", new SFSArray());
                self.send("gs_light_torch", resp, user);
                id_ += 1;
            }
        }

        id_ = 0;

        for (int x=0;x<=w;x++) {
            for (int y=0;y<=h;y++) {
                SFSObject torch = new SFSObject();
                torch.putLong("user_torch_id", id_);
                torch.putLong("user_structure", genID(x,y));
                torch.putLong("started_at", -1);
                torch.putLong("finished_at", -1);
                torch.putBool("permalit", true);


                SFSObject resp = new SFSObject();
                resp.putBool("success", true);
                resp.putSFSObject("user_torch", torch);
                resp.putLong("island_id", active);
                resp.putSFSArray("properties", new SFSArray());
                self.send("gs_light_torch", resp, user);
                id_ += 1;
            }
        }
    }
}
