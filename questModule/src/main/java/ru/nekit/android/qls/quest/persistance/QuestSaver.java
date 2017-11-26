package ru.nekit.android.qls.quest.persistance;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ru.nekit.android.qls.PreferencesUtil;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.quest.common.Quest;
import ru.nekit.android.qls.quest.types.CurrentTimeQuest;
import ru.nekit.android.qls.quest.types.FruitArithmeticQuest;
import ru.nekit.android.qls.quest.types.MetricsQuest;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;
import ru.nekit.android.qls.quest.types.PerimeterQuest;
import ru.nekit.android.qls.quest.types.TextQuest;
import ru.nekit.android.qls.quest.types.TimeQuest;
import ru.nekit.android.qls.quest.types.VisualRepresentationalNumberSummandQuest;
import ru.nekit.android.qls.utils.AbstractStateSaver;
import ru.nekit.android.qls.utils.RuntimeTypeAdapterFactory;

public class QuestSaver extends AbstractStateSaver<Quest> {

    private final String CLASS_NAME_META = "@class";
    private final String VALUE = "value";
    private final String QUEST_AVAILABLE_VARIANTS = "quest.available_variants";

    private Gson mGson, mGsonAvailableVariants;
    private JsonParser mParser;

    public QuestSaver() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        mGsonAvailableVariants = new GsonBuilder().create();
        final RuntimeTypeAdapterFactory<Quest> typeFactory = RuntimeTypeAdapterFactory
                .of(Quest.class, CLASS_NAME_META);
        for (Class<? extends Quest> classItem : getSupportsClasses()) {
            typeFactory.registerSubtype(classItem, classItem.getName());
        }
        gsonBuilder.registerTypeAdapterFactory(typeFactory);
        mGson = gsonBuilder.create();
        mParser = new JsonParser();
    }

    @Override
    public String getName() {
        return "quest";
    }

    @Override
    protected String getUUID() {
        return PreferencesUtil.getString(Pupil.NAME_CURRENT);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Quest>[] getSupportsClasses() {
        return new Class[]{
                NumberSummandQuest.class,
                PerimeterQuest.class,
                MetricsQuest.class,
                FruitArithmeticQuest.class,
                TimeQuest.class,
                TextQuest.class,
                CurrentTimeQuest.class,
                VisualRepresentationalNumberSummandQuest.class
        };
    }

    public void save(@NonNull Quest quest) {
        JsonObject jsonObjectResult = (JsonObject) mGson.toJsonTree(quest, Quest.class);
        JsonArray jsonArrayAvailableVariants = new JsonArray();
        Object[] availableVariants = quest.getAvailableAnswerVariants();
        if (availableVariants != null) {
            for (Object variantObject : availableVariants) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(CLASS_NAME_META, variantObject.getClass().getName());
                jsonObject.add(VALUE, mGsonAvailableVariants.toJsonTree(variantObject));
                jsonArrayAvailableVariants.add(jsonObject);
            }
        }
        jsonObjectResult.add(QUEST_AVAILABLE_VARIANTS, jsonArrayAvailableVariants);
        saveString(jsonObjectResult.toString());
    }

    public Quest restore() {
        JsonObject jsonObjectQuest = mParser.parse(restoreString()).getAsJsonObject();
        Quest quest = mGson.fromJson(jsonObjectQuest, Quest.class);
        JsonArray jsonArrayAvailableVariants =
                (JsonArray) jsonObjectQuest.get(QUEST_AVAILABLE_VARIANTS);
        Object[] availableVariants = new Object[jsonArrayAvailableVariants.size()];
        int index = 0;
        for (JsonElement jsonElement : jsonArrayAvailableVariants) {
            JsonObject object = (JsonObject) jsonElement;
            Class availableVariantClass = null;
            try {
                availableVariantClass = Class.forName(object.get(CLASS_NAME_META).getAsString());
            } catch (ClassNotFoundException exp) {
                exp.printStackTrace();
            }
            if (availableVariantClass != null) {
                availableVariants[index++] = mGsonAvailableVariants.fromJson(object.get(VALUE).toString(), availableVariantClass);
            }
        }
        quest.setAvailableAnswerVariants(availableVariants);
        return quest;
    }
}
