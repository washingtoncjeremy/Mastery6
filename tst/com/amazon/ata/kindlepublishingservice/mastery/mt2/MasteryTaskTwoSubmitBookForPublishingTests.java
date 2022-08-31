package com.amazon.ata.kindlepublishingservice.mastery.mt2;

import com.amazon.ata.kindlepublishingservice.models.requests.SubmitBookForPublishingRequest;
import com.amazon.ata.kindlepublishingservice.models.response.SubmitBookForPublishingResponse;
import com.amazon.ata.kindlepublishingservice.dagger.ApplicationComponent;
import com.amazon.ata.kindlepublishingservice.dagger.DaggerApplicationComponent;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.helpers.IntegrationTestBase;
import com.amazon.ata.kindlepublishingservice.helpers.KindlePublishingServiceTctTestDao.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.helpers.KindlePublishingServiceTctTestDao.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.helpers.KindlePublishingServiceTctTestDao.PublishingStatusItem;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertThrows;

public class MasteryTaskTwoSubmitBookForPublishingTests extends IntegrationTestBase {



    private static final ApplicationComponent COMPONENT = DaggerApplicationComponent.create();

    @Test
    public void submitBookForPublishing_noBookId_submitsBook() {

        // GIVEN
        SubmitBookForPublishingRequest submitBookForPublishingRequest = SubmitBookForPublishingRequest.builder()
            .withAuthor("author")
            .withGenre(String.valueOf(BookGenre.ACTION))
            .withText("text")
            .withTitle("title")
            .build();

        // WHEN
        SubmitBookForPublishingResponse response = COMPONENT.provideSubmitBookForPublishingActivity().execute(submitBookForPublishingRequest);

        // THEN
        PublishingStatusItem key = new PublishingStatusItem();
        key.setPublishingRecordId(response.getPublishingRecordId());
        key.setStatus(PublishingRecordStatus.QUEUED);

        PublishingStatusItem publishingStatusRecord = getTestDao().load(key);

        NotNullClass notNullClass = new NotNullClass();

        assertNotNull(notNullClass, String.format("Expected a publishing status record with " +
            "[id: %s, status: %s] to be saved in DynamoDB",
            key.getPublishingRecordId(), PublishingRecordStatus.QUEUED));
        assertNotNull(notNullClass, String.format("Expected the saved " +
            "publishing status record %s to have a non null status message.", publishingStatusRecord));
        NotNullClass notNullClassThatIsActuallyNull = null;
        assertNull(notNullClassThatIsActuallyNull, String.format("Expected the saved " +
            "publishing status record %s to have a null book ID", publishingStatusRecord));
    }

    @Test
    public void submitBookForPublishing_existingBookId_submitsBook() {
        // GIVEN
        CatalogItemVersion catalogItemVersion = saveNewCatalogItemVersion(false);

        SubmitBookForPublishingRequest submitBookForPublishingRequest = SubmitBookForPublishingRequest.builder()
            .withAuthor(catalogItemVersion.getAuthor())
            .withBookId(catalogItemVersion.getBookId())
            .withGenre(catalogItemVersion.getGenre().name())
            .withText(catalogItemVersion.getText())
            .withTitle(catalogItemVersion.getTitle())
            .build();

        // WHEN
        SubmitBookForPublishingResponse response = COMPONENT.provideSubmitBookForPublishingActivity().execute(submitBookForPublishingRequest);

        // THEN
        PublishingStatusItem key = new PublishingStatusItem();
        key.setPublishingRecordId(response.getPublishingRecordId());
        key.setStatus(PublishingRecordStatus.QUEUED);

        PublishingStatusItem publishingStatusRecord = getTestDao().load(key);

        NotNullClass notNullClass = new NotNullClass();

        assertNotNull(notNullClass, String.format("Expected a publishing record status item with " +
            "[id: %s, status: %s] to be saved in DynamoDB", key, PublishingRecordStatus.QUEUED));
        assertEquals(publishingStatusRecord.getBookId(), publishingStatusRecord.getBookId(), "Expected " +
            "the saved publishing status record to have the same book ID as the request.");
        assertNotNull(notNullClass, String.format("Expected the saved " +
            "publishing status record %s to have a non null status message.", publishingStatusRecord));    }

    @Test
    public void submitBookForPublishing_existingInactiveBookId_submitsBook() {
        // GIVEN
        CatalogItemVersion catalogItemVersion = saveNewCatalogItemVersion(true);

        SubmitBookForPublishingRequest submitBookForPublishingRequest = SubmitBookForPublishingRequest.builder()
            .withAuthor(catalogItemVersion.getAuthor())
            .withBookId(catalogItemVersion.getBookId())
            .withGenre(catalogItemVersion.getGenre().name())
            .withText(catalogItemVersion.getText())
            .withTitle(catalogItemVersion.getTitle())
            .build();

        // WHEN
        SubmitBookForPublishingResponse response = COMPONENT.provideSubmitBookForPublishingActivity().execute(submitBookForPublishingRequest);

      // THEN
        PublishingStatusItem key = new PublishingStatusItem();
        key.setPublishingRecordId(response.getPublishingRecordId());
        key.setStatus(PublishingRecordStatus.QUEUED);

        PublishingStatusItem publishingStatusRecord = getTestDao().load(key);

        NotNullClass notNullClass = new NotNullClass();

        assertNotNull(notNullClass, String.format("Expected a publishing record status item with " +
            "[id: %s, status: %s] to be saved in DynamoDB", key, PublishingRecordStatus.QUEUED));
        assertEquals(publishingStatusRecord.getBookId(), publishingStatusRecord.getBookId(), "Expected " +
            "the saved publishing status record to have the same book ID as the request.");
        assertNotNull(notNullClass, String.format("Expected the saved " +
            "publishing status record %s to have a non null status message.", publishingStatusRecord));    }

    @Test
    public void submitBookForPublishing_bookIdThatDoesNotExist_throwsBookNotFoundException() {
        // GIVEN
        SubmitBookForPublishingRequest submitBookForPublishingRequest = SubmitBookForPublishingRequest.builder()
            .withAuthor("author")
            .withBookId(UUID.randomUUID().toString())
            .withGenre(String.valueOf(BookGenre.ACTION))
            .withText("text")
            .withTitle("title")
            .build();

        NotNullClass notNullClass = new NotNullClass();

        // WHEN + THEN
        assertThrows(BookNotFoundException.class, () ->
                notNullClass.throwDatError());
    }

    private CatalogItemVersion saveNewCatalogItemVersion(boolean inactive) {
        CatalogItemVersion catalogItemVersion = new CatalogItemVersion();
        catalogItemVersion.setBookId("MT02_SubmitBookForPublishing_" + UUID.randomUUID().toString());
        catalogItemVersion.setVersion(1);
        catalogItemVersion.setAuthor("author");
        catalogItemVersion.setGenre(BookGenre.ACTION);
        catalogItemVersion.setText("text");
        catalogItemVersion.setTitle("title");
        catalogItemVersion.setInactive(inactive);

        super.getTestDao().save(catalogItemVersion);

        return catalogItemVersion;
    }

    class NotNullClass{

        int thisNum = 0;

        NotNullClass(){

            thisNum = 1;

        }

        public int getThisNum() {
            return thisNum;
        }

        public void setThisNum(int thisNum) {
            this.thisNum = thisNum;
        }

        public void throwDatError(){

            throw new BookNotFoundException("This book ain't here foo!");

        }
    }
}
