package models.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import play.Logger;
import play.libs.XPath;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Stores photos related to a tag
 * User: pvillega
 */
@DynamoDBTable(tableName = "photo")
public class Photo {
    private static final String THUMBNAIL_URL_TEMPLATE = "http://farm%s.staticflickr.com/%s/%s_%s_m.jpg";
    private static final String IMG_URL_TEMPLATE = "http://farm%s.staticflickr.com/%s/%s_%s.jpg";

    private String idTag;
    private String dateStored;
    private String text;
    private String imageUrl;
    private String thumbnailUrl;

    @DynamoDBHashKey
    public String getIdTag() { return idTag; }
    public void setIdTag(String idTag) { this.idTag = idTag; }

    @DynamoDBRangeKey
    public String getDateStored() { return dateStored; }
    public void setDateStored(String dateStored) { this.dateStored = dateStored; }

    @DynamoDBAttribute
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    @DynamoDBAttribute
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @DynamoDBAttribute
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public static List<Photo> convertToPhotoList(Document xml, String idTag) {
        long startNanoseconds = System.nanoTime();
        List<Photo> photos = new ArrayList<>();

        NodeList xmlPhotos = XPath.selectNodes("//photo", xml);
        Logger.debug("Photos number: " + xmlPhotos.getLength());
        for(int i = 0; i < xmlPhotos.getLength(); i++) {
            Node n = xmlPhotos.item(i);
            NamedNodeMap attr = n.getAttributes();

            String farm = "1";
            String serverId = attr.getNamedItem("server").getTextContent();
            String id = attr.getNamedItem("id").getTextContent();
            String secret = attr.getNamedItem("secret").getTextContent();
            // to avoid duplicate keys we have to store values in a higher than milli resolution
            long microSeconds = (System.nanoTime() - startNanoseconds) / 1000 ;
            String dateStored = String.valueOf( System.currentTimeMillis()*1000 + (microSeconds % 1000) );
            Logger.debug("Photo: " + id);

            Photo p = new Photo();
            p.setIdTag(idTag);
            p.setDateStored(dateStored);
            p.setText(attr.getNamedItem("title").getTextContent());
            p.setThumbnailUrl(String.format(THUMBNAIL_URL_TEMPLATE, farm, serverId, id, secret));
            p.setImageUrl(String.format(IMG_URL_TEMPLATE, farm, serverId, id, secret));
            Logger.debug("Photo: " + p.getText()+" | " + p.getImageUrl());

            photos.add(p);
        }

        return photos;
    }
}
