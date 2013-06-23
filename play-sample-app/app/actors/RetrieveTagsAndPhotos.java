package actors;

import akka.actor.UntypedActor;
import models.dynamo.Photo;
import models.dynamo.Tag;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.libs.F;
import service.DynamoDbService;

import java.util.ArrayList;
import java.util.List;

/**
 * Actor used to retrieve data in the background
 * User: pvillega
 */
public class RetrieveTagsAndPhotos extends UntypedActor {

    final String key = Play.application().configuration().getString("flickr.key");
    final String secret = Play.application().configuration().getString("flickr.secret");
    final String restUrl = Play.application().configuration().getString("flickr.url");

    /**
     * When receiving the message it connects to Flickr to retrieve a list of tags and 1 page of photos for each
     * Only stores new tags
     * @param message received message (ignored)
     */
    @Override
    public void onReceive(Object message) {
        Logger.info("Retrieving data in the background");
        final DynamoDbService db = DynamoDbService.INSTANCE;

        Logger.info("Obtain a list of tags");
        final F.Promise<play.libs.WS.Response> resp = play.libs.WS.url(restUrl)
                .setQueryParameter("method", "flickr.tags.getHotList")
                .setQueryParameter("api_key", key)
                .get();

        final play.libs.WS.Response response = resp.get();
        final List<Tag> tags = Tag.convertToTagList(response.asXml());

        Logger.info("Save tags into dynamo db");
        db.saveTags(tags);

        Logger.info("Obtain a list of photos for each tag");
        List<Photo> photos;
        for(Tag tag: tags) {
            Logger.debug("Photos for tag: " + tag.getName());
            final F.Promise<play.libs.WS.Response> respImg = play.libs.WS.url(restUrl)
                    .setQueryParameter("method", "flickr.photos.search")
                    .setQueryParameter("api_key", key)
                    .setQueryParameter("tags", tag.getName())
                    .get();

            final play.libs.WS.Response responseImg = respImg.get();
            photos = Photo.convertToPhotoList(responseImg.asXml(), tag.getId());

            Logger.info("Save photos into dynamo db");
            db.savePhotos(photos);
        }

        Logger.info("All data retrieved and stored in dynamo db");
    }
}