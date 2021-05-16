package wooteco.subway.member.dao;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.member.domain.Member;

@Repository
public class MemberDao {

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    private RowMapper<Member> rowMapper = (rs, rowNum) ->
        new Member(
            rs.getLong("id"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getInt("age")
        );


    public MemberDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("member")
            .usingGeneratedKeyColumns("id");
    }

    public Member insert(Member member) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(member);
        Long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        return new Member(id, member.getEmail(), member.getPassword(), member.getAge());
    }

    public void update(Member member) {
        String sql = "update MEMBER set email = ?, password = ?, age = ? where id = ?";
        jdbcTemplate.update(sql,
            new Object[]{member.getEmail(), member.getPassword(), member.getAge(), member.getId()});
    }

    public void deleteById(Long id) {
        String sql = "delete from MEMBER where id = ?";
        jdbcTemplate.update(sql, id);
    }

    public boolean existsById(Long id) {
        String sql = "select exists (select * from member where id = ?)";

        return jdbcTemplate.queryForObject(sql, boolean.class, id);
    }

    public Member findById(Long id) {
        String sql = "select * from MEMBER where id = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    public boolean existsByEmailAndPassword(String email, String password) {
        String sql = "select exists (select * from MEMBER where email=? AND password=?)";
        return jdbcTemplate.queryForObject(sql, boolean.class, email, password);
    }

    public Member findByEmail(String email) {
        String sql = "select * from MEMBER where email=?";
        return jdbcTemplate.queryForObject(sql, rowMapper, email);
    }
}