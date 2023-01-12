package com.zewsic.msm_server;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.smartfoxserver.v2.Main;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.zewsic.msm_server.MainExtension.*;
import static com.zewsic.msm_server.MainExtension.players;
import static com.zewsic.msm_server.StructuresManager.buy_structure;
import static com.zewsic.msm_server.UsersManager.*;
import static com.zewsic.msm_server.utils.getSFSFromJson;

public class    Commands {

    static String[] help = new String[]{
            "help - List of commands\npresets [page] - list of presets(deprecated)\npreset [num] - use preset(deprecated)\nclean - clear you account\nclone [ID] - clone account from MSM Hacks Server by id\nstatus - give server status\nonline - give server online\nforcedp - enable/disable incubator mechanics\nforcedec - enable/disable all structs in decorations\nbind [ID] - bind telegram to MSM Hacks Server Account ID\nskin [ID] [ID2] enable/disable skin(ID2) in island(ID)\nisland [ID] change island to id\ngclone (friend code) - clone account from original MSM",
            "clean_m - delete all monsters from active island\nclean_s - delete all structs from active island"
    };

    static String[] presets_list = new String[]{
            "0) Clean\n1) Mechus\n2) Gegeman"
    };

    static String[] presets = new String[]{
            "clean.json",
            "mechus.json",
            "gegeman.json"
    };

    static MainExtension x;

    public static boolean isAdmin(String user_id) {
        return Objects.equals(user_id, admin_id);
    }


    public static String ExecuteAccountCmd(MainExtension ctx, String cmd, String user_id, boolean isTelegram) {
        String[] args = cmd.split(" ");
        String resp = "";
        String telegram_id;

        if (isTelegram) {
            telegram_id = user_id;
            user_id = load_players().getSFSObject("white_list").getUtfString(user_id);
        } else {
            telegram_id = getTgIdbyMSM(user_id);
        }

        switch (args[0]) {
            case "help":
                int page;
                try {page = Integer.parseInt(args[1]);}
                catch (Exception e) {page = 1;}

                resp = "Commands:\n\n" + help[page-1] + "\n\nPage " + page + "/" + help.length;
                break;
            case "presets":
                try {page = Integer.parseInt(args[1]);}
                catch (Exception e) {page = 1;}

                resp = "Presets:\n\n" + presets_list[page-1] + "\n\nPage " + page + "/" + help.length+"\nTHIS ACTION DELETE YOU ACCOUNT";
                break;
            case "preset":
                int id;
                try {id = Integer.parseInt(args[1]);}
                catch (Exception e) {resp = "ID error";break;}

                try {
                    remove_user_data(user_id);
                    File preset_data = new File(ROOT + "presets" + File.separator + presets[id]);
                    File player_data = new File(ROOT + "players" + File.separator + user_id + ".json");
                    create_new_user(player_data, preset_data, user_id);
                } catch (Exception e) {
                    resp = "Load error.";
                    break;
                }

                resp = "ReLogin now.";
                break;
            case "clone":
                String clone_id;
                try {clone_id = args[1];}
                catch (Exception e) {resp = "ID error";break;}

                try {
                    remove_user_data(user_id);
                    File preset_data = new File(ROOT + "players" + File.separator + clone_id + ".json");
                    File player_data = new File(ROOT + "players" + File.separator + user_id + ".json");
                    create_new_user(player_data, preset_data, user_id);
                } catch (Exception e) {
                    resp = "Clone error.";
                    break;
                }

                resp = "ReLogin now.";
                break;
            case "gclone":
                try {clone_id = args[1];}
                catch (Exception e) {resp = "incorrect friend code";break;}

                try {
                    x.trace("Getting empty data");
                    SFSObject empty = getSFSFromJson(new File(ROOT + "db_files" + File.separator + "gs_player.json"));

                    MSMClient msm = new MSMClient();
                    x.trace("Auth in client");
                    x.trace(msm.auth("funtube.free-acc@ya.ru", "funTUBE"));
                    x.trace("Locating server");
                    x.trace(msm.locateServer());
                    x.trace("Authing in server");
                    msm.authInServer("get_friend");
                    try {TimeUnit.SECONDS.sleep(5);} catch (InterruptedException e) {throw new RuntimeException(e);}
                    x.trace("Getting friend data");
                    SFSObject friend = msm.get_friend(clone_id,x);

                    x.trace("Converting");
                    SFSObject player = (SFSObject) empty.getSFSObject("player_object");
                    player.putUtfString("display_name", friend.getUtfString("display_name"));

                    SFSArray player_islands = new SFSArray();
                    SFSArray islands = (SFSArray) friend.getSFSArray("islands");
                    for (int i=0;i<islands.size();i++) {
                        x.trace("Island: " + i);
                        SFSObject player_island = new SFSObject();
                        SFSObject island = (SFSObject) islands.getSFSObject(i);

                        player_island.putSFSArray("eggs", new SFSArray());
                        player_island.putDouble("warp_speed", 1.0d);
                        player_island.putInt("island", island.getInt("island"));
                        player_island.putLong("dislikes", 0);
                        player_island.putLong("likes", 0);
                        player_island.putSFSArray("fuzer", new SFSArray());
                        player_island.putSFSArray("baking", new SFSArray());
                        player_island.putSFSArray("costumes_owned", new SFSArray());
                        player_island.putSFSArray("breeding", new SFSArray());
                        player_island.putSFSArray("torches", new SFSArray());
                        player_island.putInt("last_player_level", 75);
                        player_island.putInt("num_torches", 0);
                        player_island.putLong("user_island_id", 1000 + island.getInt("island"));
                        player_island.putLong("user", 100000000);
                        player_island.putInt("type", island.getInt("island"));


                        SFSArray monsters = (SFSArray) island.getSFSArray("monsters");
                        SFSArray player_monsters = new SFSArray();
                        for (int m=0;m<monsters.size();m++) {
                            x.trace("Monster " + m);
                            SFSObject player_monster = new SFSObject();
                            SFSObject monster = (SFSObject) monsters.getSFSObject(m);

                            player_monster.putDouble("volume", monster.getDouble("volume"));
                            player_monster.putInt("pos_x", monster.getInt("pos_x"));
                            player_monster.putInt("pos_y", monster.getInt("pos_y"));
                            player_monster.putInt("flip", monster.getInt("flip"));
                            player_monster.putInt("level", 21);
                            player_monster.putInt("happiness", 100);
                            player_monster.putUtfString("collection_type", "coins");
                            player_monster.putUtfString("name", "Made by @msm_hacks");
                            try{player_monster.putSFSObject("megamonster", monster.getSFSObject("megamonster"));} catch (Exception ignored){}
                            player_monster.putInt("in_hotel", monster.getInt("in_hotel"));
                            player_monster.putInt("muted", monster.getInt("muted"));
                            player_monster.putLong("user_monster_id", monster.getLong("user_monster_id"));
                            player_monster.putLong("monster", monster.getInt("monster"));
                            player_monster.putLong("last_collection", System.currentTimeMillis());
                            player_monster.putLong("island", 1000 + island.getInt("island"));

                            player_monsters.addSFSObject(player_monster);
                        }
                        player_island.putSFSArray("monsters", player_monsters);

                        SFSArray structures = (SFSArray) island.getSFSArray("structures");
                        SFSArray player_structures = new SFSArray();
                        for (int s=0;s<structures.size();s++) {
                            x.trace("Structure " + s);
                            SFSObject player_structure = new SFSObject();
                            SFSObject structure = (SFSObject) structures.getSFSObject(s);

                            player_structure.putLong("island", 1000 + island.getInt("island"));
                            player_structure.putInt("book_value", 100);
                            player_structure.putInt("pos_x", structure.getInt("pos_x"));
                            player_structure.putInt("pos_y", structure.getInt("pos_y"));
                            player_structure.putInt("flip", structure.getInt("flip"));
                            player_structure.putInt("muted", 0);
                            player_structure.putInt("in_warehouse", structure.getInt("in_warehouse"));
                            player_structure.putInt("is_upgrading", 0);
                            player_structure.putInt("is_complete", 1);
                            player_structure.putLong("user_structure_id", structure.getLong("user_structure_id"));
                            player_structure.putLong("last_collection", System.currentTimeMillis());
                            player_structure.putInt("structure", structure.getInt("structure"));
                            player_structure.putDouble("scale", structure.getDouble("scale"));

                            player_structures.addSFSObject(player_structure);
                        }
                        player_island.putSFSArray("structures", player_structures);

                        player_islands.addSFSObject(player_island);
                    }
                    player.putSFSArray("islands", player_islands);
                    empty.putSFSObject("player_object", player);

                    x.trace("Saving");
                    save_user_data(user_id, empty);
                    x.trace("Done");
                } catch (Exception e) {
                    resp = "Clone error: " + e;
                    break;
                }

                resp = "ReLogin now.";
                break;
            case "clean":
                remove_user_data(user_id);
                resp = "Success. ReLogin now.";
                break;
            case "fill": {
                try {
                    id = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    resp = "Error ID";
                    break;
                }

                Random rnd = new Random(System.currentTimeMillis());

                SFSObject user_ = load_user_data(user_id);
                SFSObject user = (SFSObject) user_.getSFSObject("player_object");
                long active = user.getLong("active_island");
                SFSArray islands = (SFSArray) user.getSFSArray("islands");
                for (int i = 0; i < islands.size(); i++) {
                    SFSObject island = (SFSObject) islands.getSFSObject(i);
                    if (island.getLong("user_island_id") == active) {

                        SFSArray structures = (SFSArray) island.getSFSArray("structures");

                        for (int xx = 0; xx < 50; xx++) {
                            for (int yy = 0; yy < 50; yy++) {

                                SFSObject structure = new SFSObject();
                                structure.putLong("island", active);
                                structure.putInt("book_value", 100);
                                structure.putInt("flip", 0);
                                structure.putInt("muted", 0);
                                structure.putInt("in_warehouse", 0);
                                structure.putInt("is_upgrading", 0);
                                structure.putInt("is_complete", 1);
                                structure.putLong("last_collection", System.currentTimeMillis());
                                structure.putDouble("scale", 1.0);
                                structure.putInt("pos_x", xx);
                                structure.putInt("pos_y", yy);
                                structure.putInt("structure", id);
                                structure.putLong("user_structure_id", rnd.nextInt(999999 - 10000 + 1));
                                structures.addSFSObject(structure);
                            }
                        }

                        island.putSFSArray("structures", structures);
                        islands.removeElementAt(i);
                        islands.addSFSObject(island);
                    }
                }
                user.putSFSArray("islands", islands);
                user_.putSFSObject("player_object", user);
                save_user_data(user_id, user_);


                resp = "ReLogin now.";
                break;
            }
            case "clear_s":
            case "clean_s":
                SFSObject user_ = load_user_data(user_id);
                SFSObject user = (SFSObject) user_.getSFSObject("player_object");
                long active = user.getLong("active_island");
                SFSArray islands = (SFSArray) user.getSFSArray("islands");
                for (int i = 0; i < islands.size();i++) {
                    SFSObject island = (SFSObject) islands.getSFSObject(i);
                    if (island.getLong("user_island_id") == active) {
                        island.putSFSArray("structures", new SFSArray());
                    }
                }
                user.putSFSArray("islands", islands);
                user_.putSFSObject("player_object", user);
                save_user_data(user_id, user_);
                resp = "ReLogin now.";
                break;
            case "clear_m":
            case "clean_m":
                user_ = load_user_data(user_id);
                user = (SFSObject) user_.getSFSObject("player_object");
                active = user.getLong("active_island");
                islands = (SFSArray) user.getSFSArray("islands");
                for (int i = 0; i < islands.size();i++) {
                    SFSObject island = (SFSObject) islands.getSFSObject(i);
                    if (island.getLong("user_island_id") == active) {
                        island.putSFSArray("monsters", new SFSArray());
                    }
                }
                user.putSFSArray("islands", islands);
                user_.putSFSObject("player_object", user);
                save_user_data(user_id, user_);
                resp = "ReLogin now.";
                break;
            case "status":
                java.io.File dir = new java.io.File(ROOT + "players"); //path указывает на директорию
                List<File> lst = new ArrayList<>();
                for ( java.io.File file : Objects.requireNonNull(dir.listFiles())){
                    if ( file.isFile() )
                        lst.add(file);
                }

                resp = "MSM Hacks Server status: \n\n" +
                        "Enable: " + ("YES") + "\n" +
                        "Work time: " + (Math.round((System.currentTimeMillis()-ctx.start_time)/1000/60) +" минут") + "\n" +
                        "MITM mode: " + (transparent_mode?"YES":"NO") + "\n" +
                        "For testers: " + (!isDev?"YES":"NO") + "\n" +
                        "Accounts count: " + (lst.size()) + "\n" +
                        "Cirrent online: " + (SmartFoxServer.getInstance().getUserManager().getUserCount()+ "/" + players_limit) + "\n";
                break;
            case "online":
                String pls = "";
                int i = 1;
                for (String us: players) {pls += i + ". " + inWhiteList(us, isTelegram) + "\n";i++;}
                resp = "Cirrent online: " + (SmartFoxServer.getInstance().getUserManager().getUserCount()) + "/"+players_limit+"\n\n" + pls;
                break;
            case "bind":
                resp  = "Command only for telegram-bot: @msm_hacks_server_bot";
                break;
            case "forcedp":
                SFSObject player = load_user_data(user_id);
                if (player.containsKey("force_dp")) {
                    player.putBool("force_dp",  !player.getBool("force_dp"));
                } else player.putBool("force_dp",  true);
                save_user_data(user_id, player);
                resp = "" + ((player.getBool("force_dp")?"Enable":"Disable") + "\n\nReLogin now.");
                break;
            case "forcedec":
                player = load_user_data(user_id);
                if (player.containsKey("force_dec")) {
                    player.putBool("force_dec",  !player.getBool("force_dec"));
                } else player.putBool("force_dec",  true);
                save_user_data(user_id, player);
                resp = "" + ((player.getBool("force_dec")?"Enable":"Disable") + "\n\nReLogin now.");
                break;
            //commands with __ only for admin
            case "__binds_off":
                if (!isAdmin(telegram_id)) {
                    resp = "Access denied.";
                    break;
                }
                isFree = false;
                resp = "Binds disabled";
                break;
            case "__binds_on":
                if (!isAdmin(telegram_id)) {
                    resp = "Access denied.";
                    break;
                }
                isFree = true;
                resp = "Binds enabled";
                break;
            case "__dev_mode":
                if (!isAdmin(telegram_id)) {
                    resp = "Access denied.";
                    break;
                }
                isDev = true;
                resp = "Dev mode enabled";
                break;
            case "__play_mode":
                if (!isAdmin(telegram_id)) {
                    resp = "Access denied.";
                    break;
                }
                isDev = false;
                resp = "Dev mode disabled";
                break;
            case "__online":
                if (!isAdmin(telegram_id)) {
                    resp = "Access denied.";
                    break;
                }
                int iid;
                try{iid = Integer.parseInt(args[1]);} catch (Exception e) {resp="error";break;}
                players_limit = iid;
                resp = "New max online is " + iid;
                break;
            case "__msg_game":
                if (!isAdmin(telegram_id)) {
                    resp = "Access denied.";
                    break;
                }
                String ad_text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                MainExtension.ad_game = ad_text;
                resp = "Msg in game set: \n\n" + ad_text;
                break;
            case "__msg_tg":
                if (!isAdmin(telegram_id)) {
                    resp = "Access denied.";
                    break;
                }
                ad_text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                ad_tg = ad_text;
                resp = "Msg in tg set: \n\n" + ad_text;
                break;
            case "__send_msg_game":
                if (!isAdmin(telegram_id)) {
                    resp = "Access denied.";
                    break;
                }
                ad_text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                for (User userx: SmartFoxServer.getInstance().getUserManager().getAllUsers()) {
                    SFSObject response = new SFSObject();
                    response.putBool("force_logout", false);
                    response.putUtfString("msg", ad_text);
                    MainExtension.con.send("gs_display_generic_message", response, userx);
                }
                resp = "Msg is sended " + SmartFoxServer.getInstance().getUserManager().getUserCount() + " players";
                break;
            case "__kick_all":
                if (!isAdmin(telegram_id)) {
                    resp = "Access denied.";
                    break;
                }
                ad_text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    for (User userx: SmartFoxServer.getInstance().getUserManager().getAllUsers()) {
                        SFSObject response = new SFSObject();
                        response.putBool("force_logout", true);
                        response.putUtfString("msg", ad_text);
                        MainExtension.con.send("gs_display_generic_message", response, userx);
                    }
                resp = "Kicked " + SmartFoxServer.getInstance().getUserManager().getUserCount() + " players";
                break;
            case "__send_msg_tg":
                if (!isAdmin(telegram_id)) {
                    resp = "Access denied.";
                    break;
                }
                ad_text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                SFSObject white_list = (SFSObject) load_players().getSFSObject("white_list");
                for (String tg_id : white_list.getKeys())
                    TelegramBot.bot.execute(new SendMessage(tg_id, ad_text).parseMode(ParseMode.HTML));
                resp = "Msg is sended " + load_players().getSFSObject("white_list").getKeys().size() + " players";
                break;
            case "skin":
                int iid2;
                boolean on=true;
                try{iid = Integer.parseInt(args[1]);} catch (Exception e) {iid = 6;}
                if (iid>6||iid<1){
                    resp = "Skin ID is not 1-5!";
                    break;
                }
                try{iid2 = Integer.parseInt(args[2]);} catch (Exception e) {iid2 = 23;}
                if (iid2>22||iid<1){
                    resp = "Island id is not 1-22!";
                    break;
                }

                SFSObject player_ = load_user_data(user_id);
                player = (SFSObject) player_.getSFSObject("player_object");
                SFSObject skin = (SFSObject) SFSObject.newFromJsonData("{\"t\":"+iid+",\"i\":"+iid2+"}");
                SFSArray themes = (SFSArray) player.getSFSArray("active_island_themes");
                for (i=0;i< themes.size();i++) {
                    SFSObject theme = (SFSObject) themes.getSFSObject(i);
                    if (theme == skin) {
                        themes.removeElementAt(i);
                        on=false;
                    }
                }
                if (on) {
                    themes.addSFSObject(skin);
                }

                player.putSFSArray("active_island_themes",  themes);
                player_.putSFSObject("player_object", player);
                save_user_data(user_id, player_);
                resp = "Skin status: " + (on?"Enabled":"Disabled") + "\n\nReLogin now.";
                break;
            case "island":
                try{iid = Integer.parseInt(args[1]);} catch (Exception e) {iid = 23;}
                if (iid>22||iid<1){
                    resp = "Island id is not 1-22";
                    break;
                }

                player_ = load_user_data(user_id);
                player = (SFSObject) player_.getSFSObject("player_object");
                player.putInt("active_island",  1000+iid);
                player_.putSFSObject("player_object", player);
                save_user_data(user_id, player_);
                resp = "Island changed. ReLogin now.";
                break;
            default:
                resp = "Unknown command";
                break;
        }
        return resp;
    }
    //gs_get_friend_visit_data
    //{"user_id":69709112}
}
