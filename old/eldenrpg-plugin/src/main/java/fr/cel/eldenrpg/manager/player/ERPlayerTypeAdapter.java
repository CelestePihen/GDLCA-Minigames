package fr.cel.eldenrpg.manager.player;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fr.cel.eldenrpg.EldenRPG;
import fr.cel.eldenrpg.manager.quest.Quest;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.*;

public class ERPlayerTypeAdapter extends TypeAdapter<ERPlayer> {

    @Override
    public void write(JsonWriter writer, ERPlayer player) throws IOException {
        writer.beginObject();

        writer.name("uuid").value(player.getPlayerUUID().toString());

        writer.name("activeQuests").beginArray();
        for (Quest quest : player.getActiveQuests()) {
            writer.beginObject();
            writer.name("id").value(quest.getId());
            writer.endObject();
        }
        writer.endArray();

        writer.name("finishedQuests").beginArray();
        for (Quest quest : player.getFinishedQuests()) {
            writer.beginObject();
            writer.name("id").value(quest.getId());
            writer.endObject();
        }
        writer.endArray();

        writer.name("completedQuests").beginArray();
        for (Quest quest : player.getCompletedQuests()) {
            writer.beginObject();
            writer.name("id").value(quest.getId());
            writer.endObject();
        }
        writer.endArray();

        writer.name("hFirstFirecampActivated").value(player.isHFirstFirecampActivated());
        writer.name("hPassThroughBlockActivated").value(player.isHPassThroughBlockActivated());

        writer.endObject();
    }

    @Override
    public ERPlayer read(JsonReader reader) throws IOException {
        UUID uuid = null;

        String questId = null;
        Quest quest = null;
        Set<Quest> activeQuests = new HashSet<>();
        Set<Quest> finishedQuests = new HashSet<>();
        Set<Quest> completedQuests = new HashSet<>();

        boolean hFirstFirecampActivated = false;
        boolean hPassThroughBlockActivated = false;

        reader.beginObject();

        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "uuid" -> uuid = UUID.fromString(reader.nextString());
                case "activeQuests" -> {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();

                        while (reader.hasNext()) {
                            if (reader.nextName().equals("id")) {
                                questId = reader.nextString();
                            }
                            quest = EldenRPG.getEldenRPG().getQuestManager().getQuestById(questId);
                            activeQuests.add(quest);
                        }

                        reader.endObject();
                    }
                    reader.endArray();
                }
                case "finishedQuests" -> {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();

                        while (reader.hasNext()) {
                            if (reader.nextName().equals("id")) {
                                questId = reader.nextString();
                            }
                            quest = EldenRPG.getEldenRPG().getQuestManager().getQuestById(questId);
                            finishedQuests.add(quest);
                        }

                        reader.endObject();
                    }
                    reader.endArray();
                }
                case "completedQuests" -> {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();

                        while (reader.hasNext()) {
                            if (reader.nextName().equals("id")) {
                                questId = reader.nextString();
                            }
                            quest = EldenRPG.getEldenRPG().getQuestManager().getQuestById(questId);
                            completedQuests.add(quest);
                        }

                        reader.endObject();
                    }
                    reader.endArray();
                }
                case "hFirstFirecampActivated" -> hFirstFirecampActivated = reader.nextBoolean();
                case "hPassThroughBlockActivated" -> hPassThroughBlockActivated = reader.nextBoolean();
            }
        }

        reader.endObject();

        ERPlayer erPlayer = new ERPlayer(Bukkit.getPlayer(uuid));
        erPlayer.setActiveQuests(activeQuests);
        erPlayer.setFinishedQuests(finishedQuests);
        erPlayer.setCompletedQuests(completedQuests);

        erPlayer.setHFirstFirecampActivated(hFirstFirecampActivated);
        erPlayer.setHPassThroughBlockActivated(hPassThroughBlockActivated);

        return erPlayer;
    }

}