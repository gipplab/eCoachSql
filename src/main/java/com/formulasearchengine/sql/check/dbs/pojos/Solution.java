package com.formulasearchengine.sql.check.dbs.pojos;

import java.io.File;

public class Solution {
    public String userName;
    public String givenName;
    public String sn;
    public Integer userId;
    public Integer solutionId;
    public File location;

    /**
     * Parses soultion from praktomat
     * <p>
     * // ARGS:
     * // 0: env.tmpdir(),
     * // 1: str(env.user().id),
     * // 2: str(env.user().mat_number),
     * // 3: str(env.user().first_name),
     * // 4: str(env.user().last_name),
     * // 5: str(env.solution().id)]
     *
     */
    public Solution(String[] args) {

        assert args.length == 6;

        this.location = new File(args[0]);
        this.userId = Integer.valueOf(args[1]);
        this.userName = args[2];
        this.givenName = args[3];
        this.sn = args[4];
        this.solutionId = Integer.valueOf(args[5]);
    }

    public Solution(File location, String givenName, String sn) {
        this.givenName = givenName;
        this.sn = sn;
        this.location = location;
    }
}