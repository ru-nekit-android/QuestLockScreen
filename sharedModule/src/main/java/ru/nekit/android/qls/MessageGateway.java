package ru.nekit.android.qls;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

import ru.nekit.android.qls.quest.TypedMessage;
import ru.nekit.android.qls.quest.history.QuestHistoryItem;

import static ru.nekit.android.qls.InternalCommand.NAME_COMMAND;
import static ru.nekit.android.qls.InternalCommand.NAME_DATA;

public class MessageGateway {

    private static final String PUBLISH_KEY = "pub-c-f61c0f10-8955-4181-bad1-e646eae3635f";
    private static final String SUBSCRIBE_KEY = "sub-c-989025f0-66c1-11e7-b6db-02ee2ddab7fe";

    private Gson gson;
    private Pubnub pubNub;
    private MessageChannel channel;
    private MessageListener messageListener;
    private Callback mMessageGatewayListener = new
            Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    super.successCallback(channel, message);
                    JSONObject object = (JSONObject) message;
                    Object data;
                    try {
                        String messageType = object.getString(TypedMessage.NAME_TYPE);
                        switch (messageType) {

                            case QuestHistoryItem.NAME:

                                data = gson.fromJson(object.get(TypedMessage.NAME_DATA).toString(),
                                        QuestHistoryItem.class);

                                break;

                            case InternalCommand.NAME:

                                JSONObject internalMessageObject =
                                        object.getJSONObject(TypedMessage.NAME_DATA);
                                String command = null;
                                Object internalCommandData = null;
                                try {
                                    command = internalMessageObject.getString(NAME_COMMAND);
                                    if (internalMessageObject.get(NAME_DATA) != null) {
                                        Class clazz = Class.forName(internalMessageObject.getString(InternalCommand.NAME_DATA_CLASS));
                                        internalCommandData = gson.fromJson(internalMessageObject.getString(NAME_DATA), clazz);
                                    }
                                } catch (ClassNotFoundException exp) {
                                    exp.printStackTrace();
                                }
                                data = new InternalCommand(command, internalCommandData);

                                break;

                            default:

                                data = new Object();

                                break;

                        }

                        if (messageListener != null) {
                            TypedMessage typedMessage = new TypedMessage(messageType, data);
                            messageListener.onMessageReceive(typedMessage,
                                    MessageGateway.this.channel);
                        }
                    } catch (JSONException exp) {
                        exp.printStackTrace();
                    }
                }
            };

    public MessageGateway(@NonNull MessageChannel channel) {
        pubNub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY, false);
        GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.registerTypeAdapterFactory(new ClassTypeAdapterFactory());
        //gsonBuilder.registerTypeAdapter(Class.class, new ClassTypeAdapter());
        gson = gsonBuilder.create();
        this.channel = channel;
    }

    public void listen(MessageListener messageListener) {
        try {
            this.messageListener = messageListener;
            pubNub.subscribe(channel.getName(), mMessageGatewayListener);
        } catch (PubnubException exp) {
            exp.printStackTrace();
        }
    }

    public void send(@NonNull String messageType, @NonNull Object data) {
        TypedMessage message = new TypedMessage(messageType, data);
        JSONObject object = null;
        try {
            object = new JSONObject(gson.toJson(message));
        } catch (JSONException exp) {
            exp.printStackTrace();
        }
        pubNub.publish(channel.getName(), object, false, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                super.successCallback(channel, message);
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                super.errorCallback(channel, error);
            }

        });
    }

    public void destroy() {
        pubNub.disconnectAndResubscribe();
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public interface MessageListener {

        void onMessageReceive(@NonNull TypedMessage typedMessage, @NonNull MessageChannel messageChannel);

    }

    /*class ClassTypeAdapter extends TypeAdapter<Class<?>> {

        @Override
        public void write(JsonWriter out, Class<?> value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.value(value.getString());
        }

        @Override
        public Class<?> read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            Class<?> clazz;
            try {
                clazz = Class.forName(jsonReader.nextString());
            } catch (ClassNotFoundException exception) {
                throw new IOException(exception);
            }
            return clazz;
        }
    }

    public class ClassTypeAdapterFactory implements TypeAdapterFactory {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            if (!Class.class.isAssignableFrom(typeToken.getRawType())) {
                return null;
            }
            return (TypeAdapter<T>) new ClassTypeAdapter();
        }
    }*/
}