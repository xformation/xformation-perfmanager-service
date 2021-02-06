/*
 * */
package com.synectiks.process.common.plugins.views.migrations;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.migrations.Migration;

import org.bson.Document;

import javax.inject.Inject;
import java.time.ZonedDateTime;

import static com.mongodb.client.model.Filters.eq;

public class V20191204000000_RemoveLegacyViewsPermissions extends Migration {
    private final MongoDatabase mongoDatabase;

    @Inject
    public V20191204000000_RemoveLegacyViewsPermissions(MongoConnection mongoConnection) {
        this.mongoDatabase = mongoConnection.getMongoDatabase();
    }

    @Override
    public ZonedDateTime createdAt() {
        return ZonedDateTime.parse("2019-12-04T00:00:00Z");
    }

    @Override
    public void upgrade() {
        MongoCollection<Document> roles = mongoDatabase.getCollection("roles");

        Document viewsUserRole = roles.findOneAndDelete(eq("name", "Views User"));

        if (viewsUserRole != null) {
            removeRoleFromUsers(viewsUserRole);
        }
    }

    private void removeRoleFromUsers(Document viewsUserRole) {
        MongoCollection<Document> users = mongoDatabase.getCollection("users");
        users.updateMany(
                new Document().append("roles", viewsUserRole.get("_id")),
                new Document().append("$pull", new Document().append("roles", viewsUserRole.get("_id"))));
    }
}
