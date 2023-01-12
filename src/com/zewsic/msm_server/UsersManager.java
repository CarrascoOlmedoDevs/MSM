package com.zewsic.msm_server;

import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.response.GetChatMemberResponse;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Objects;

import static com.zewsic.msm_server.MainExtension.ROOT;
import static com.zewsic.msm_server.utils.*;

public class UsersManager {

    public static void update_user_string(String user_id, String key, String value) {

        SFSObject data = load_user_data(user_id);

        SFSObject pl_data = (SFSObject) data.getSFSObject("player_object");
        pl_data.putUtfString(key, value);

        data.putSFSObject("player_object", pl_data);
        save_user_data(user_id, data);
    }

    public static void update_user_long(String user_id, String key, long value) {

        SFSObject data = load_user_data(user_id);

        SFSObject pl_data = (SFSObject) data.getSFSObject("player_object");
        pl_data.putLong(key, value);

        data.putSFSObject("player_object", pl_data);
        save_user_data(user_id, data);
    }

    //{"newName":"Pivo228"}
    static SFSObject update_user_name(String user_id, SFSObject input) {
        String name = input.getUtfString("newName");
        update_user_string(user_id, "display_name", name);

        SFSObject resp = new SFSObject();
        resp.putBool("success", true);
        resp.putUtfString("displayName", name);
        return resp; //{"success":true,"displayName":"Pivo228"}
    }

    public static SFSObject load_user_data(String user_id) {
        File empty_player_data = new File(ROOT + "db_files" + File.separator + "gs_player.json");
        File player_data = new File(ROOT + "players"  + File.separator +  user_id + ".json");

        if (!player_data.exists())
            create_new_user(player_data, empty_player_data, user_id);

        return getSFSFromJson(player_data);
    }

    public static ArrayList<SFSObject> load_all_user_data(String user_id) {
        File empty_player_data = new File(ROOT + "db_files" + File.separator + "gs_player.json");
        File player_data = new File(ROOT + "players"  + File.separator +  user_id + ".json");

        if (!player_data.exists())
            create_new_user(player_data, empty_player_data, user_id);

        ArrayList<SFSObject> send_data = new ArrayList<>();
        ArrayList<SFSObject> data_to_send = new ArrayList<>();

        SFSObject player_all_data_ = getSFSFromJson(player_data);
        SFSObject player_all_data = (SFSObject) player_all_data_.getSFSObject("player_object");

        //add actual value for diamonds, keys, relics, food, ethereal_currency, coins, starpower
        player_all_data.putLong("diamonds_actual", player_all_data.getLong("diamonds"));
        player_all_data.putLong("keys_actual", player_all_data.getLong("keys"));
        player_all_data.putLong("relics_actual", player_all_data.getLong("relics"));
        player_all_data.putLong("food_actual", player_all_data.getLong("food"));
        player_all_data.putLong("ethereal_currency_actual", player_all_data.getLong("ethereal_currency"));
        player_all_data.putLong("coins_actual", player_all_data.getLong("coins"));
        player_all_data.putLong("starpower_actual", player_all_data.getLong("starpower"));




        //SFSArray player_islands = (SFSArray) player_all_data.getSFSArray("islands");
//
        //player_all_data.putSFSArray("islands",  new SFSArray());
        //player_all_data_.putSFSObject("player_object", player_all_data);
        //send_data.add(player_all_data_);
//
        //for (int i=0;i<player_islands.size();i++) {
//
        //    SFSArray oneIsland = new SFSArray();
        //    oneIsland.addSFSObject(player_islands.getSFSObject(i));
        //    player_all_data.putSFSArray("islands", oneIsland);
//
        //    SFSObject tmpData = new SFSObject();
        //    tmpData.putSFSObject("player_object", player_all_data);
        //    send_data.add(tmpData);
        //}
//
        //int chunkCount = send_data.size();
        //for (int i=0;i<chunkCount;i++) {
        //    SFSObject el = send_data.get(i);
        //    el.putInt("chunk", i+1);
        //    el.putInt("numChunks", chunkCount);
//
        //    data_to_send.add(el);
        //}

        player_all_data_.putSFSObject("player_object", player_all_data);
        data_to_send.add(player_all_data_);
        return data_to_send;
    }

    public static SFSObject load_players() {
        File pl = new File(ROOT + "players.json");
        return getSFSFromJson(pl);
    }

    public static void save_players(SFSObject pla) {
        File pl = new File(ROOT + "players.json");
        putSFSToJson(pl, pla);
    }

    public static String inWhiteList(String user_id,  boolean useLink) {
        String granted = MainExtension.isFree?"Гость":"false";
        SFSObject white_list = (SFSObject) load_players().getSFSObject("white_list");
        for (String tg_id:white_list.getKeys())
            if (Objects.equals(white_list.getUtfString(tg_id), user_id)) {
                GetChatMemberResponse response = TelegramBot.bot.execute(new GetChatMember("-1001692898082", Long.parseLong(tg_id)));
                granted = (response.chatMember().user().firstName() + (response.chatMember().user().lastName()==null?"":" "+response.chatMember().user().lastName()));
                if (useLink) granted = "<a href=\"tg://user?id=" + response.chatMember().user().id() + "\">" + granted + "</a>";
            }
        return granted;
    }

    public static String getTgIdbyMSM(String user_id) {
        String granted = MainExtension.isFree?"Гость":"false";
        SFSObject white_list = (SFSObject) load_players().getSFSObject("white_list");
        for (String tg_id:white_list.getKeys())
            if (Objects.equals(white_list.getUtfString(tg_id), user_id)) {
                granted = tg_id;
            }
        return granted;
    }

    public static String inWhiteList(String user_id) {return inWhiteList(user_id, false);}

    public static void save_user_data(String user_id, SFSObject data) {
        File pl = new File(ROOT + "players"  + File.separator +  user_id + ".json");
        putSFSToJson(pl, data);
    }

    public static void remove_user_data(String user_id) {
        File pl = new File(ROOT + "players"  + File.separator +  user_id + ".json");
        pl.delete();
    }

    public static void create_new_user(File pl, File em, String user_id) {
        copyFile(em, pl);
        SFSObject data = load_user_data(user_id);

        SFSObject pl_data = (SFSObject) data.getSFSObject("player_object");
        pl_data.putUtfString("display_name", user_id);

        data.putSFSObject("player_object", pl_data);
        putSFSToJson(pl, data);
    }


}
