package com.zewsic.msm_server;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetChatMemberResponse;
import com.pengrad.telegrambot.response.SendResponse;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.zewsic.msm_server.MainExtension.*;
import static com.zewsic.msm_server.UsersManager.*;
import static com.zewsic.msm_server.utils.ReadFile;

public class TelegramBot {
    public static MainExtension ctx;
    static com.pengrad.telegrambot.TelegramBot bot;
    public static void init() {
        bot.setUpdatesListener(updates -> {
            try {
                Update update = updates.get(0);
                String msg = update.message().text();
                long chat_id = update.message().chat().id();
                int msg_id = update.message().messageId();
                boolean isChat = false;
                String text = "";
                try {msg = msg.substring(0, msg.indexOf("@"));isChat=true;} catch (Exception ignored) {}

                ctx.trace(update);

                if (!(msg.charAt(0) == '/')) {
                    if (!isChat) bot.execute(new SendMessage(chat_id, "use /help for get commands list").parseMode(ParseMode.HTML));
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                };
                msg=msg.substring(1);
                ctx.trace(msg);

                if (msg.contains("bind")) {
                    GetChatMemberResponse response = bot.execute(new GetChatMember("-1001796072285", update.message().from().id()));
                    String status = response.chatMember().status().toString();
                    if (Objects.equals(status, "member") || Objects.equals(status, "creator") || Objects.equals(status, "administrator")) {//}if (true){
                        String user_game_id = msg.split(" ")[1].toLowerCase();
                        String grant = inWhiteList(user_game_id);
                        if (grant == "false") {
                            SFSObject pla = load_players();
                            SFSObject wht = (SFSObject) pla.getSFSObject("white_list");
                            wht.putUtfString(update.message().from().id().toString(), user_game_id);
                            pla.putSFSObject("white_list", wht);
                            save_players(pla);
                            text = "Bind success: " + user_game_id;
                        } else {
                            text = "Account is already binded to  " + grant;
                        }
                    } else {
                        text = "Need to subscribe to channels before i give access to this action:\n\nhttps://t.me/msm_hacks";
                    }
                }else if (msg.contains("auth")) {
                    try {
                        MSMClient player = new MSMClient();
                        String a = msg.split(" ")[1].toLowerCase().split(":")[0];
                        String b = msg.split(" ")[1].toLowerCase().split(":")[1];

                        String auth1 = player.auth(a, b);
                        String auth2 = player.locateServer();

                        text = a + ": \n\n" + auth1 + "\n\n" + auth2;
                    } catch (Exception e) {text = String.valueOf(e);}
                } else if (msg.contains("check")) {
                    GetChatMemberResponse response = bot.execute(new GetChatMember("-1001692898082", update.message().from().id()));
                    String status = response.chatMember().status().toString();
                    if (Objects.equals(status, "creator")) {
                        String data = ReadFile(new File(ROOT + "login_data.txt"));
                        ArrayList<String[]> users = new ArrayList<>();
                        String[] datas = data.split("\n");
                        for(String user : datas) {
                            users.add(user.split(":"));
                        }
                        for (int p=0;p<users.size();p++) {
                            MSMClient player = new MSMClient();
                            String auth1 = player.auth(users.get(p)[0], users.get(p)[1]);
                            String auth2 = player.locateServer();
                            text = users.get(p)[0] + ": " + auth1;
                            SendResponse sr = bot.execute(new SendMessage(chat_id, text).parseMode(ParseMode.HTML));
                        }

                    } else {
                        text = "Доступ запрещен. \n\nВаш статус: " + status;
                    }
                } else {
                    text = Commands.ExecuteAccountCmd(ctx, msg, String.valueOf(update.message().from().id()), true);
                }

                boolean finalIsChat = isChat;
                String finalText = text;
                new Thread(() -> {
                    SendResponse sr = bot.execute(new SendMessage(chat_id, finalText).parseMode(ParseMode.HTML));

                    int bmsg_id = sr.message().messageId();
                    try {TimeUnit.SECONDS.sleep(20);} catch (InterruptedException e) {throw new RuntimeException(e);}
                    TelegramBot.bot.execute(new DeleteMessage("-1001692898082", bmsg_id));
                }).start();



            } catch (Exception ignored) {}
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

        new Thread(() -> {
            SendResponse sr = bot.execute(new SendMessage(statusChat, getStatText()).parseMode(ParseMode.HTML));
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(15);
                    bot.execute(new EditMessageText(statusChat, sr.message().messageId(), getStatText()).parseMode(ParseMode.HTML));
                } catch (Exception e) {
                    sr = bot.execute(new SendMessage(statusChat, getStatText()).parseMode(ParseMode.HTML));
                }
            }
        }).start();
    }

    public static String getStatText() {
        java.io.File dir = new java.io.File(ROOT + "players"); //path указывает на директорию
        List<File> lst = new ArrayList<>();
        for ( java.io.File file : Objects.requireNonNull(dir.listFiles())){
            if ( file.isFile() )
                lst.add(file);
        }

        String pls = "";
        int i = 1;
        //for (String us: players) {pls += i + ". " + inWhiteList(us, true) + "\n";i++;}
        String online_data = "Онлайн сервера: " + (SmartFoxServer.getInstance().getUserManager().getUserCount()) + "/"+players_limit+"\n" + pls;

        Date date = new Date ();
        date.setTime(System.currentTimeMillis());

        return "Сервер запущен!" +
                "\n\nВремя работы: " + (Math.round((System.currentTimeMillis()-ctx.start_time)/1000/60) +" минут") + "\n" +
                "Прозрачный режим: " + (transparent_mode?"YES":"NO") + "\n" +
                "Доступен тестерам: " + (!isDev?"YES":"NO") + "\n" +
                "Количество аккаунтов: " + (lst.size()) + "\n\n" +
                online_data + "\n\n"+
                "Обновлено: " + date;
    }

    public static void send_message(long chat_id, String text) {
        SendMessage request = new SendMessage(chat_id, text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .disableNotification(true)
                .replyToMessageId(1)
                .replyMarkup(new ForceReply());

        bot.execute(request, new Callback<SendMessage, SendResponse>() {
            @Override
            public void onResponse(SendMessage request, SendResponse response) {

            }

            @Override
            public void onFailure(SendMessage request, IOException e) {

            }
        });


    }

    //[Update{update_id=864329261, message=Message{message_id=4, from=User{id=1349833467, is_bot=false,
    // first_name='╨Ь╨╛╤В╤П', last_name='null', username='Bambi161119', language_code='ru', is_premium='null',
    // added_to_attachment_menu='null', can_join_groups=null, can_read_all_group_messages=null, supports_inline_queries=null},
    // sender_chat=null, date=1659004784, chat=Chat{id=1349833467, type=Private, first_name='╨Ь╨╛╤В╤П', last_name='null',
    // username='Bambi161119', title='null', photo=null, bio='null', has_private_forwards=null, join_to_send_messages=null,
    // join_by_request=null, description='null', invite_link='null', pinned_message=null, permissions=null, slow_mode_delay=null,
    // message_auto_delete_time=null, has_protected_content=null, sticker_set_name='null', can_set_sticker_set=null,
    // linked_chat_id=null, location=null}, forward_from=null, forward_from_chat=null, forward_from_message_id=null,
    // forward_signature='null', forward_sender_name='null', forward_date=null, is_automatic_forward=null, reply_to_message=null,
    // via_bot=null, edit_date=null, has_protected_content=null, media_group_id='null', author_signature='null',
    // text='/start', entities=[MessageEntity{type=bot_command, offset=0, length=6, url='null', user=null, language='null'}],
    // caption_entities=null, audio=null, document=null, animation=null, game=null, photo=null, sticker=null, video=null,
    // voice=null, video_note=null, caption='null', contact=null, location=null, venue=null, poll=null, dice=null,
    // new_chat_members=null, left_chat_member=null, new_chat_title='null', new_chat_photo=null, delete_chat_photo=null,
    // group_chat_created=null, supergroup_chat_created=null, channel_chat_created=null, message_auto_delete_timer_changed=null,
    // migrate_to_chat_id=null, migrate_from_chat_id=null, pinned_message=null, invoice=null, successful_payment=null,
    // connected_website='null', passport_data=null, proximity_alert_triggered=null, video_chat_started=null, video_chat_ended=null,
    // video_chat_participants_invited=null, video_chat_scheduled=null, reply_markup=null, web_app_data=null}, edited_message=null,
    // channel_post=null, edited_channel_post=null, inline_query=null, chosen_inline_result=null, callback_query=null, shipping_query=null,
    // pre_checkout_query=null, poll=null, poll_answer=null, my_chat_member=null, chat_member=null, chat_join_request=null}]

}
