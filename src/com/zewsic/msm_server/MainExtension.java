package com.zewsic.msm_server;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.ISFSEventListener;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;
import com.zewsic.msm_server.fun.Screen;
import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.requests.ExtensionRequest;
import sfs2x.client.requests.LoginRequest;
import sfs2x.client.util.ConfigData;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.zewsic.msm_server.Commands.ExecuteAccountCmd;
import static com.zewsic.msm_server.FunManager.play_scratch;
import static com.zewsic.msm_server.IslandsManager.*;
import static com.zewsic.msm_server.MSMClient.processRequest;
import static com.zewsic.msm_server.MonstersManager.*;
import static com.zewsic.msm_server.StructuresManager.*;
import static com.zewsic.msm_server.UsersManager.*;
import static com.zewsic.msm_server.utils.getSFSFromJson;
import static com.zewsic.msm_server.utils.putSFSToJson;


public class MainExtension extends SFSExtension {

    public void test() {}

    String server_ip = "54.80.58.36";
    String user_id = "s3mnbkyv4p";
    String user_token = "3WuIxHCZcQOkkX2lk5593cM/0rF2tCXiRMzjoOSVFbE4u6oLKcN2dXD+43nrNf2OZVJmc0MMal+QN3J0pBG5D53q/z9FEj7ZNZZlZvC+NBuapM80Y8GxW68whQg/YWAdC6Xe9SF6DJzDFbQB5lub9dMkgIwp63JNwbmHBP6tV+OoIpZNsgdwxdwtaDcV++t4N1Y3YsRg/mo8QSdR1Z8p4h013jymCK31DZbaS2rbqFtv8WofNzt1yjPOth7Se8M=";

    long start_time;
    static ArrayList<String> players = new ArrayList<>();
    static boolean transparent_mode = false;
    static boolean isDev = false;
    static boolean isLinux = true;
    User trans_user = null;
    SmartFox sfs = null;
    static String logChat = "";
    static String statusChat = "-1001777763891";
    static boolean isFree = false;
    static int players_limit = 480;
    public static String admin_id = "";
    static String tg_token = "5480875440:AAFGw10fVAKbEqc_MfJwbI_iR4WGoUQgQpA";
    static String ad_game = "Thanks for playing!\n\nPlease, support me on Boosty: https://boosty.to/zewsic";
    static String ad_tg = "Thanks for playing!\n\nPlease, support me on Boosty: https://boosty.to/zewsic";
    static int cache_size = 100;
    static MainExtension con;

    final static String ROOT = isLinux?"/root/server/":"C:\\Users\\Zewsic\\SmartFoxServer_2X\\SFS2X\\";

    public void loadConfig() {
        File file = new File(ROOT + "config.json");
        if (file.exists()) {
            SFSObject sfsObject = getSFSFromJson(new File(ROOT + "config.json"));
            server_ip = sfsObject.getUtfString("server_ip");
            players_limit = sfsObject.getInt("players_limit");
            ad_game = sfsObject.getUtfString("ad_game");
            ad_tg = sfsObject.getUtfString("ad_tg");
            isFree = sfsObject.getBool("isFree");
            transparent_mode = sfsObject.getBool("transparent_mode");
            isDev = sfsObject.getBool("isDev");
            isLinux = sfsObject.getBool("isLinux");
            logChat = sfsObject.getUtfString("logChat");
            statusChat = sfsObject.getUtfString("statusChat");
            admin_id = sfsObject.getUtfString("admin_id");
            tg_token = sfsObject.getUtfString("tg_token");
            cache_size = sfsObject.getInt("cache_size");
        }
        else {
            SFSObject sfsObject = new SFSObject();
            sfsObject.putUtfString("server_ip", server_ip);
            sfsObject.putInt("players_limit", players_limit);
            sfsObject.putUtfString("ad_game", ad_game);
            sfsObject.putUtfString("ad_tg", ad_tg);
            sfsObject.putBool("isFree", isFree);
            sfsObject.putBool("transparent_mode", transparent_mode);
            sfsObject.putBool("isDev", isDev);
            sfsObject.putBool("isLinux", isLinux);
            sfsObject.putUtfString("logChat", logChat);
            sfsObject.putUtfString("statusChat", statusChat);
            sfsObject.putUtfString("admin_id", admin_id);
            sfsObject.putUtfString("tg_token", tg_token);
            sfsObject.putInt("cache_size", cache_size);
            putSFSToJson(new File(ROOT + "config.json"), sfsObject);
        }
    }

    @Override
    public void init() {
        test();
        Commands.x = this;
        con = this;
        start_time = System.currentTimeMillis();

        loadConfig();


        MSMClient initData = new MSMClient();
        trace(initData.auth("ropggtop225@gmail.com", "asd123456cxz"));
        trace(initData.locateServer());

        if (transparent_mode) {
            server_ip = initData.server_ip;
            user_id = initData.user_id;
            user_token = initData.user_token;
        }
        try {initData.authInServer("load_dbs");} catch (Exception e) {
            trace("Error: " + e.getMessage());
        }


        TelegramBot.ctx = this;
        new Thread(() -> {
               TelegramBot.bot = new com.pengrad.telegrambot.TelegramBot(tg_token);
               TelegramBot.init();
        }).start();
        addEventListener(SFSEventType.USER_JOIN_ZONE, new PlayerJoinListener());
        addEventListener(SFSEventType.USER_DISCONNECT, new PlayerLogoutListener());
    }

    public static class PlayerLogoutListener implements ISFSEventListener {

        @Override
        public void handleServerEvent(ISFSEvent isfsEvent) throws Exception {
            User user = (User) isfsEvent.getParameter(SFSEventParam.USER);
            for (int h=0;h<20;h++) players.remove(user.getName());
            String grant = inWhiteList(user.getName(), true);
            if (!(Objects.equals(grant, "false"))) {
                TelegramBot.bot.execute(new SendMessage(logChat, grant + " вышел из сервера!").parseMode(ParseMode.HTML));
            }
        }
    }

    public class PlayerJoinListener implements ISFSEventListener {

        @Override
        public void handleServerEvent(ISFSEvent isfsEvent) {


            if (SmartFoxServer.getInstance().getUserManager().getUserCount()>players_limit) {
                SFSObject ban = new SFSObject();
                ban.putUtfString("reason",  "Server is full. Please try again later.");
                //ban.putUtfString("reason",  "Вас наебали.\n\nNMSMYT просто воруют мои моды и другие фичи, выставляя их за свои.\n\nt.me/msm_hacks");
                send("gs_player_banned", ban, (User) isfsEvent.getParameter(SFSEventParam.USER));
                new Thread(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {throw new RuntimeException(e);}
                    sfsApi.disconnectUser((User) isfsEvent.getParameter(SFSEventParam.USER));
                }).start();
            } else {
                SFSObject response = new SFSObject();
                response.putBool("force_logout", false);
                response.putUtfString("msg", "You successfully joined to the server!.\nOnline: " + (SmartFoxServer.getInstance().getUserManager().getUserCount()+ "/" + players_limit));
                send("gs_display_generic_message", response, (User) isfsEvent.getParameter(SFSEventParam.USER));
            }

            if (transparent_mode) {
                trans_user = (User) isfsEvent.getParameter(SFSEventParam.USER);
                ConfigData cfg = new ConfigData();
                cfg.setHost(server_ip);
                cfg.setPort(9339);
                cfg.setZone("Test Zone");
                cfg.setDebug(false);

                // Set up event handlers
                sfs = new SmartFox();
                sfs.addEventListener(SFSEvent.CONNECTION, this::onConnection);
                sfs.addEventListener(SFSEvent.HANDSHAKE, this::onHandshake);
                sfs.addEventListener(SFSEvent.EXTENSION_RESPONSE, this::onResponse);
                sfs.addEventListener(SFSEvent.LOGIN, this::onLogin);
                sfs.addEventListener(SFSEvent.LOGIN_ERROR, this::onLogin);

                // Connect to server
                sfs.connect(cfg);
            } else {
                Object user = isfsEvent.getParameter(SFSEventParam.USER);
                String grant = inWhiteList(((User) user).getName(), true);
                if (!Objects.equals(grant, "false")) {
                    send("gs_initialized", new SFSObject(), (User) user);
                    new Thread(() -> TelegramBot.bot.execute(new SendMessage(logChat, grant + " зашёл на сервер!").parseMode(ParseMode.HTML))).start();
                    players.add(((User) user).getName());
                }
                else {
                    SFSObject ban = new SFSObject();
                    ban.putUtfString("reason",  "To get FREE access to the server, use command  \"/bind " + ((User) user).getName() + "\" in @msm_hacks_server_bot (telegram)");
                    //ban.putUtfString("reason",  "Вас наебали.\n\nNMSMYT просто воруют мои моды и другие фичи, выставляя их за свои.\n\nt.me/msm_hacks");
                    send("gs_player_banned", ban, (User) user);
                    new Thread(() -> {
                        TelegramBot.bot.execute(new SendMessage(logChat, "Попытка зайти без лицензии: " + ((User) user).getName()));
                        try {
                            TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {throw new RuntimeException(e);}
                        sfsApi.disconnectUser((User) user);
                    }).start();

                }
            }

        }
        private void onConnection(BaseEvent baseEvent) {
            trace("(TRANSPARENT MODE) Connected to server!");
            trace(baseEvent.toString());
        }

        private void onHandshake(BaseEvent baseEvent) {
            trace(baseEvent.toString());
            trace("(TRANSPARENT MODE) Handshake successful!");

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

        private void onResponse(BaseEvent baseEvent) {
            var params = baseEvent.getArguments();
            trace("TRANSPARENT RESPONSE: " + params.get("cmd").toString());
            trace(((SFSObject) params.get("params")).toJson());
            if (params.get("cmd").toString().contains("gs_player_banned"))
                send("gs_initialized", new SFSObject(), trans_user);
            else
                send(params.get("cmd").toString(), processRequest(params.get("cmd").toString(), (SFSObject) params.get("params")), trans_user);
            putSFSToJson(new File(ROOT + "saves" + File.separator + params.get("cmd").toString() + ".json"), (SFSObject) params.get("params"));
        }

        private void onLogin(BaseEvent baseEvent) {
            trace("(TRANSPARENT MODE) Loginned to server!");
            trace(baseEvent.getArguments().toString());
        }
    }


    public void send_database(String cmd, User sender, boolean useChunk) {
        File file = new File(ROOT + "db_files" + File.separator + cmd + ".json");
        if (Objects.equals(cmd, "db_island")) {
            SFSObject player = load_user_data(sender.getName());
            SFSObject oneData = getSFSFromJson(file);
            boolean fdp = true;

            if (player.containsKey("force_dp")) {fdp = player.getBool("force_dp");}
            trace("FDP: " + fdp);
            if (fdp) {
                SFSArray datas = (SFSArray) oneData.getSFSArray("islands_data");
                SFSArray dats = new SFSArray();
                for (int i = 0; i < datas.size(); i++) {
                    SFSObject data = (SFSObject) datas.getSFSObject(i);
                    data.putInt("island_type", 10);
                    data.putInt("has_book", 0);
                    data.putInt("island_lock", 1);
                    dats.addSFSObject(data);
                }
                oneData.putSFSArray("islands_data", dats);
            }
            send(cmd, oneData, sender);
            return;
        }
        if (Objects.equals(cmd, "db_structure")) {
            SFSObject player = load_user_data(sender.getName());
            SFSObject oneData = getSFSFromJson(file);
            boolean fdp = true;
            if (fdp) {
                SFSArray datas = (SFSArray) oneData.getSFSArray("structures_data");
                SFSArray dats = new SFSArray();
                for (int i = 0; i < datas.size(); i++) {
                    SFSObject data = (SFSObject) datas.getSFSObject(i);
                    data.putUtfString("structure_type", "decoration");
                    dats.addSFSObject(data);
                }
                oneData.putSFSArray("structures_data", dats);
            }
            send(cmd, oneData, sender);
            for (int i = 2; i < 10; i++) {
                file = new File(ROOT + "db_files" + File.separator + cmd + "_" + i + ".json");
                trace(file.toPath().toAbsolutePath().toString());
                oneData = getSFSFromJson(file);

                if (oneData == null) break;
                if (fdp) {
                    SFSArray datas = (SFSArray) oneData.getSFSArray("structures_data");
                    SFSArray dats = new SFSArray();
                    for (int u = 0; u < datas.size(); u++) {
                        SFSObject data = (SFSObject) datas.getSFSObject(u);
                        data.putUtfString("structure_type", "decoration");
                        dats.addSFSObject(data);
                    }
                    oneData.putSFSArray("structures_data", dats);
                }
                send(cmd, oneData, sender);
            }
            return;
        }
        send(cmd, getSFSFromJson(file), sender);

        if (useChunk) {
            for (int i = 2; i < 10; i++) {
                file = new File(ROOT + "db_files" + File.separator + cmd + "_" + i + ".json");
                SFSObject resp = getSFSFromJson(file);

                if (resp == null) break;
                send(cmd, resp, sender);
            }
        }
    }

    String noCmd = "Неизвестная команда. Используйте help для прсмотра списка команд. Команды можно вводить в телегрвам боте сервера @msm_hacks_server_bot, или напрямую в игре, в меню ввода реферального кода.";
    String[] other_bases  = new String[]{"gs_quest", "gs_epic_monster_data", "gs_rare_monster_data", "gs_timed_events", "gs_store_replacements",
            "gs_monster_island_2_island_data", "gs_entity_alt_cost_data", "gs_cant_breed"};
    List<String> other_bases_files = new ArrayList<>(Arrays.asList(other_bases));

    @Override
    public void handleClientRequest(String cmd, User sender, ISFSObject params) {
        new Thread(() -> handleClientRequest(cmd, sender, params, 0)).start();
    }

    public void handleClientRequest(String cmd, User sender, ISFSObject params, int gg) {
        if (transparent_mode) {
            trace("TRANSPARENT REQUEST: " + cmd);
            trace(params.toJson());
            sfs.send(new ExtensionRequest(cmd, params));
        } else {
            trace("Request: ", cmd, sender, params);
            String user_id = sender.getName();
            trace(params.toJson());

            if (isDev) {
                SFSObject response = new SFSObject();
                response.putBool("force_logout", true);
                response.putUtfString("msg", "Server mode is changed to development.\n\n" + sender.getName());
                send("gs_display_generic_message", response, sender);
                return;
            }
            
            if (cmd.contains("db_")) {
                send_database(cmd, sender, true);
            } else if (other_bases_files.contains(cmd)) {
                send_database(cmd, sender, false);
            } else if (cmd.equals("gs_player")) {
                for (SFSObject all_datum : load_all_user_data(user_id)) send(cmd, all_datum, sender);

                if (!(ad_game == null)) {
                        SFSObject response = new SFSObject();
                        response.putBool("force_logout", false);
                        response.putUtfString("msg", ad_game);

                        try {
                            TimeUnit.SECONDS.sleep(10);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        send("gs_display_generic_message", response, sender);
                }
                if (!(ad_tg == null)) {
                        String grant = getTgIdbyMSM(user_id);
                        TelegramBot.bot.execute(new SendMessage(grant, ad_tg).parseMode(ParseMode.HTML));
                }
            } else {
                switch (cmd) {
                    case "gs_change_island":
                        send(cmd, change_island(user_id, (SFSObject) params), sender);
                    case "gs_buy_island":
                        send(cmd, buy_island(user_id, (SFSObject) params), sender);
                        break;
                    case "gs_save_island_warp_speed":
                        send(cmd, set_warp_island(user_id, (SFSObject) params), sender);
                        break;
                    case "gs_set_displayname":
                        send(cmd, update_user_name(user_id, (SFSObject) params), sender);
                        break;
                    case "gs_sell_structure":
                        send(cmd, sell_structure(user_id, (SFSObject) params), sender);
                        break;
                    case "gs_move_structure": {
                        SFSObject[] resps = move_structure(user_id, (SFSObject) params);
                        send(cmd, resps[0], sender);
                        send("gs_update_structure", resps[1], sender);
                        break;
                    }
                    case "gs_flip_structure": {
                        SFSObject[] resps = flip_structure(user_id, (SFSObject) params);
                        send(cmd, resps[0], sender);
                        send("gs_update_structure", resps[1], sender);
                        break;
                    }
                    case "gs_buy_structure":
                        SFSObject resp = buy_structure(user_id, (SFSObject) params);
                        send(cmd, resp, sender);
                        //send("gs_update_structure", resps[1], sender);
                        break;
                    case "gs_flip_monster": {
                        SFSObject[] resps = flip_monster(user_id, (SFSObject) params);
                        send(cmd, resps[0], sender);
                        send("gs_update_monster", resps[1], sender);
                        break;
                    }
                    case "gs_move_monster": {
                        SFSObject[] resps = move_monster(user_id, (SFSObject) params);
                        send(cmd, resps[0], sender);
                        send("gs_update_monster", resps[1], sender);
                        break;
                    }
                    case "gs_sell_monster":
                        send(cmd, sell_monster(user_id, (SFSObject) params), sender);
                        break;
                    case "gs_name_monster":
                        send(cmd, name_monster(user_id, (SFSObject) params), sender);
                        break;
                    case "gs_collect_monster": {
                        SFSObject[] resps = collect_monster(user_id, (SFSObject) params);
                        send(cmd, resps[0], sender);
                        send("gs_update_monster", resps[1], sender);
                        break;
                    }
                    case "gs_mute_monster": {
                        SFSObject[] resps = mute_monster(user_id, (SFSObject) params);
                        send(cmd, resps[0], sender);
                        send("gs_update_monster", resps[1], sender);
                        break;
                    }
                    case "gs_mega_monster_message": {
                        SFSObject[] resps = scale_monster(user_id, (SFSObject) params);
                        send("gs_update_monster", resps[1], sender);
                        send(cmd, resps[0], sender);
                        break;
                    }
                    case "gs_buy_egg":
                        send(cmd, buy_egg(user_id, (SFSObject) params), sender);
                        break;
                    case "gs_hatch_egg":
                        send(cmd, hatch_egg(user_id, (SFSObject) params), sender);
                        break;
                    case "gs_refresh_tribe_requests":
                        // Тут должен был быть код но мне лень его писать и вообще я хз что он делает так что пофиг
                        break;
                    case "gs_player_has_scratch_off": //
                        send(cmd, SFSObject.newFromJsonData("{\"success\":true}"), sender);
                        break;
                    case "gs_start_upgrade_structure":
                        SFSObject[] resps = wtf_structure(user_id, (SFSObject) params);
                        send("gs_update_structure", resps[1], sender);
                        send(cmd, resps[0], sender);
                        break;
                    case "gs_play_scratch_off":
                    case "gs_purchase_scratch_off": //
                        send(cmd, play_scratch(user_id, (SFSObject) params), sender);
                        break;
                    case "gs_get_code":
                        SFSObject command = new SFSObject();
                        String sl = params.getUtfString("code").toLowerCase().substring(2);
                        command.putUtfString("message", (ExecuteAccountCmd(this, sl, user_id,false)));
                        command.putUtfString("cmd", cmd);
                        command.putBool("success", false);
                        send(cmd, command, sender);
                        break;
                    default:
                        SFSObject error = new SFSObject();
                        error.putUtfString("message", cmd + " is not implemented yet!");
                        error.putUtfString("cmd", cmd);
                        error.putBool("success", false);
                        send(cmd, error, sender);

                        //trace(params.toJson());
                        break;
                }
            }
        }

    }   // {"user_structure_id":360,"success":true,"properties":
    // []}


}
