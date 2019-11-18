package active_lead.dto;

import active_lead.models.User;

public class UserAuthDTO implements DTO {

    String password;
    String name;
    Boolean isAdmin;

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public static UserAuthDTO from(User user) {
        return UserAuthDTO.newBuilder().setPassword(user.getPassword()).setName(user.getName()).setIsAdmin(user.getAdmin()).build();
    }

    public static Builder newBuilder() {
        return new UserAuthDTO().new Builder();
    }

    public class Builder {

        private Builder() { }

        public Builder setPassword(java.lang.String password) {
            UserAuthDTO.this.password = password;
            return this;
        }

        public Builder setName(java.lang.String name) {
            UserAuthDTO.this.name = name;
            return this;
        }

        public Builder setIsAdmin(java.lang.Boolean isAdmin) {
            UserAuthDTO.this.isAdmin = isAdmin;
            return this;
        }

        public UserAuthDTO build() {
            return UserAuthDTO.this;
        }
    }
}
