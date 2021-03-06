package io.vertx.axle.mysql;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import io.vertx.axle.core.Vertx;
import io.vertx.axle.mysqlclient.MySQLPool;
import io.vertx.axle.sqlclient.Pool;
import io.vertx.axle.sqlclient.RowSet;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.PoolOptions;

public class MySQLClientTest {
    private static final String MYSQL_ROOT_PASSWORD = "my-secret-pw";
    private static final String MYSQL_DATABASE = "test";

    @Rule
    public GenericContainer<?> container = new GenericContainer<>("mysql:5")
            .withExposedPorts(3306)
            .withEnv("MYSQL_ROOT_PASSWORD", MYSQL_ROOT_PASSWORD)
            .withEnv("MYSQL_DATABASE", MYSQL_DATABASE);

    private Vertx vertx;

    @Before
    public void setUp() {
        vertx = Vertx.vertx();
        assertThat(vertx).isNotNull();
    }

    @After
    public void tearDown() {
        vertx.close();
    }

    @Test
    public void testAxleAPI() {
        MySQLConnectOptions options = new MySQLConnectOptions()
                .setPort(container.getMappedPort(3306))
                .setHost(container.getContainerIpAddress())
                .setDatabase(MYSQL_DATABASE)
                .setUser("root")
                .setPassword(MYSQL_ROOT_PASSWORD);

        Pool client = MySQLPool.pool(vertx, options, new PoolOptions().setMaxSize(5));

        RowSet<?> join = client.query("SELECT 1")
                .execute()
                .toCompletableFuture().join();
        assertThat(join).isNotNull();
        assertThat(join.size()).isEqualTo(1);
    }
}
