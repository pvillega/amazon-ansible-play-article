package service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.*;
import models.dynamo.Photo;
import models.dynamo.Tag;
import play.Logger;
import play.Play;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Service class to ease interaction with DynamoDB
 * Singleton
 * User: pvillega
 */
public enum DynamoDbService {
    INSTANCE;

    // Load AWS keys from configuration
    private String accessKey = Play.application().configuration().getString("aws.accessKey");
    private String secretKey = Play.application().configuration().getString("aws.secretKey");
    private String endpoint = Play.application().configuration().getString("aws.endpoint");

    // Set up connection to Dynamo DB
    private AWSCredentials awsCredentials;
    private AmazonDynamoDB dynamo;
    private DynamoDBMapper mapper;

    DynamoDbService() {
        awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        dynamo = new AmazonDynamoDBClient(awsCredentials);
        dynamo.setEndpoint(endpoint);
        mapper = new DynamoDBMapper(dynamo);
    }

    public List<Tag> getAllTags() {
        final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        final List<Tag> tags = mapper.scan(Tag.class, scanExpression);
        return tags;
    }

    public void saveTags(List<Tag> tags) {
        mapper.batchSave(tags);
    }

    public List<Photo> getAllPhotos() {
        final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        final List<Photo> photos = mapper.scan(Photo.class, scanExpression);
        return photos;
    }

    public void savePhotos(List<Photo> photos) {
        mapper.batchSave(photos);
    }

    public List<Photo> getPhotosForTag(String tagId) {
        Photo phKey = new Photo();
        phKey.setIdTag(tagId);

        long yesterdayMilli = (new Date()).getTime() - (24L*60L*60L*1000L);
        long yesterdayMicro = yesterdayMilli * 1000L;

        final Condition rangeKeyCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.GT.toString())
                .withAttributeValueList(new AttributeValue().withS( String.valueOf(yesterdayMicro) ));


        final DynamoDBQueryExpression<Photo> queryExpression = new DynamoDBQueryExpression<Photo>()
                .withHashKeyValues(phKey)
                .withRangeKeyCondition("dateStored", rangeKeyCondition);

        List<Photo> taggedPhotos = mapper.query(Photo.class, queryExpression);
        return taggedPhotos;
    }
}
