package com.bj.eduteacher.videoplayer.model;

/**
 * Created by Administrator on 2018/6/25 0025.
 */

public class AliyunVideoInfo {
    private String RequestId;
    private AssumedRoleUserBean AssumedRoleUser;
    private CredentialsBean Credentials;

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String RequestId) {
        this.RequestId = RequestId;
    }

    public AssumedRoleUserBean getAssumedRoleUser() {
        return AssumedRoleUser;
    }

    public void setAssumedRoleUser(AssumedRoleUserBean AssumedRoleUser) {
        this.AssumedRoleUser = AssumedRoleUser;
    }

    public CredentialsBean getCredentials() {
        return Credentials;
    }

    public void setCredentials(CredentialsBean Credentials) {
        this.Credentials = Credentials;
    }

    public static class AssumedRoleUserBean {
        /**
         * AssumedRoleId : 356809364916964407:RamTestUpload
         * Arn : acs:ram::1460581335009402:role/ramtestreadonly/RamTestUpload
         */

        private String AssumedRoleId;
        private String Arn;

        public String getAssumedRoleId() {
            return AssumedRoleId;
        }

        public void setAssumedRoleId(String AssumedRoleId) {
            this.AssumedRoleId = AssumedRoleId;
        }

        public String getArn() {
            return Arn;
        }

        public void setArn(String Arn) {
            this.Arn = Arn;
        }
    }

    public static class CredentialsBean {
        /**
         * AccessKeySecret : HHsy8bx1rj2qiLEhboYsxsX6TkESNNrEcxcebFfjXK5b
         * AccessKeyId : STS.NJbZMHGa37KZBMW2ipdcbycaa
         * Expiration : 2018-06-24T07:55:20Z
         * SecurityToken : CAISjQJ1q6Ft5B2yfSjIr4nXEff8qr4SgImxQGvm1mklaOxOlqbKgzz2IH1KdHFhAu4av/0xlW5U6v4dlqRJQpp5SFfYUNN06Z1bqeJbomRq/57b16cNrbH4M1zxYkeJ462/SuH9S8ynP5XJQlvYlyh17KLnfDG5JTKMOoGIjpgVE7Z3WRKjPxVLGJU0Rwx5s50+NGDNd4zaUHjQj3HXEVBjtydllGp78t7f+MCH7QfEh1CIoY185aaJe8T6N5Q8ZcwkDo/tjbAuLJCsinAAt0J4k45tl7FB9Dv9udWQPkJc+R3uMZCPr4M/dF4iOPRrQvAV8qSgzaJi3fbakpj60ApXMOhZVCLbVZAyOC0RsQcNXBqAAVuAy2ui2/93wS6ci7Pu07+gbGDDRRn9z9YowiSAkYzpAAnI5nOmhFshVJDYP1LwFjeHmt145Dctw5Bfu0zKP+uNUuSjJj6iIGVKeZEEtVRwZOEWU8tzdmZ0qI/vZMc0T3kKmeiVCZc5FObd1k/SANKJn8x+FAF+xEYq5gw4jDr6
         */

        private String AccessKeySecret;
        private String AccessKeyId;
        private String Expiration;
        private String SecurityToken;

        public String getAccessKeySecret() {
            return AccessKeySecret;
        }

        public void setAccessKeySecret(String AccessKeySecret) {
            this.AccessKeySecret = AccessKeySecret;
        }

        public String getAccessKeyId() {
            return AccessKeyId;
        }

        public void setAccessKeyId(String AccessKeyId) {
            this.AccessKeyId = AccessKeyId;
        }

        public String getExpiration() {
            return Expiration;
        }

        public void setExpiration(String Expiration) {
            this.Expiration = Expiration;
        }

        public String getSecurityToken() {
            return SecurityToken;
        }

        public void setSecurityToken(String SecurityToken) {
            this.SecurityToken = SecurityToken;
        }
    }
}
