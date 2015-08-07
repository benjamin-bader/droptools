package com.bendb.example.resources;

import com.bendb.dropwizard.jooq.jersey.JooqInject;
import com.bendb.example.core.BlogPost;
import com.bendb.example.db.tables.PostTag;
import com.bendb.example.db.tables.records.BlogPostRecord;
import com.bendb.example.db.tables.records.PostTagRecord;
import io.dropwizard.jersey.params.IntParam;
import org.joda.time.DateTime;
import org.jooq.*;
import org.jooq.impl.DSL;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.Arrays;
import java.util.List;

import static com.bendb.dropwizard.jooq.PostgresSupport.arrayAgg;
import static com.bendb.example.db.Tables.BLOG_POST;
import static com.bendb.example.db.Tables.POST_TAG;
import static org.jooq.impl.DSL.*;

@Path("posts")
@Produces(MediaType.APPLICATION_JSON)
public class PostsResource {

    @GET
    public List<BlogPost> findPostsByTag(@QueryParam("tag") String tag, @JooqInject("slave") DSLContext slave) {
        final PostTag pt = POST_TAG.as("pt");

        // Try *that* in JPA
        return slave.with("postIds").as(
                    select(POST_TAG.POST_ID.as("id"))
                        .from(POST_TAG)
                        .where(POST_TAG.TAG_NAME.equal(DSL.lower(tag))))
                .select(BLOG_POST.ID, BLOG_POST.BODY, BLOG_POST.CREATED_AT, arrayAgg(pt.TAG_NAME))
                .from(BLOG_POST)
                .leftOuterJoin(pt)
                .on(BLOG_POST.ID.equal(pt.POST_ID))
                .whereExists(selectOne()
                        .from(tableByName("postIds"))
                        .where(field("id").equal(BLOG_POST.ID)))
                .groupBy(BLOG_POST.ID, BLOG_POST.BODY, BLOG_POST.CREATED_AT)
                .orderBy(BLOG_POST.CREATED_AT.desc())
                .fetch(new RecordMapper<Record4<Integer, String, DateTime, String[]>, BlogPost>() {
                    @Override
                    public BlogPost map(Record4<Integer, String, DateTime, String[]> record) {
                        final Integer postId = record.value1();
                        final String text = record.value2();
                        final DateTime createdAt = record.value3();
                        final List<String> tags = Arrays.asList(record.value4());

                        return BlogPost.create(postId, text, createdAt, tags);
                    }
                });
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createPost(
            final @FormParam("text") String text,
            final @FormParam("tags") List<String> tags,
            @JooqInject("master") DSLContext master) {
        // TODO(ben): implement something like @UnitOfWork
        return master.transactionResult(new TransactionalCallable<Response>() {
            @Override
            public Response run(Configuration configuration) throws Exception {
                DSLContext cxt = DSL.using(configuration);

                BlogPostRecord post = cxt
                        .insertInto(BLOG_POST, BLOG_POST.BODY)
                        .values(text)
                        .returning(BLOG_POST.ID, BLOG_POST.CREATED_AT)
                        .fetchOne();

                if (tags != null && tags.size() > 0) {
                    InsertValuesStep2<PostTagRecord, Integer, String> insert = cxt.insertInto(
                            POST_TAG, POST_TAG.POST_ID, POST_TAG.TAG_NAME);

                    for (String tag : tags) {
                        insert = insert.values(post.getId(), tag);
                    }

                    insert.execute();
                }

                return Response.ok()
                        .location(UriBuilder.fromPath("/" + post.getId()).build())
                        .build();
            }
        });
    }

    @GET
    @Path("/{id}")
    public BlogPost getPost(@PathParam("id") IntParam id, @Context DSLContext create) {
        final Record4<Integer, String, DateTime, String[]> record = create
                .select(BLOG_POST.ID, BLOG_POST.BODY, BLOG_POST.CREATED_AT, arrayAgg(POST_TAG.TAG_NAME))
                .from(BLOG_POST)
                .leftOuterJoin(POST_TAG)
                .on(BLOG_POST.ID.equal(POST_TAG.POST_ID))
                .where(BLOG_POST.ID.equal(id.get()))
                .groupBy(BLOG_POST.ID, BLOG_POST.BODY, BLOG_POST.CREATED_AT)
                .fetchOne();

        if (record == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        final Integer postId = record.value1();
        final String text = record.value2();
        final DateTime createdAt = record.value3();
        final List<String> tags = Arrays.asList(record.value4());

        return BlogPost.create(postId, text, createdAt, tags);
    }
}
