package nhsmedic.com.tcc.nhsappmedic.institution.model;

public class HealthInstitutionModel {


    private String idHealthInstitution;

    private String identityCode;

    private String name;
    private String postalCode;


    private String state;

    private String city;

    private String street;

    private String neighborhood;

    private String number;

    private String photo;

    private String telephone;


    public static class HealthInstitutionModelBuilder {

        private String idHealthInstitution;
        private String identityCode;
        private String name;
        private String postalCode;
        private String state;
        private String city;
        private String street;
        private String neighborhood;
        private String number;
        private String photo;
        private String telephone;

        public HealthInstitutionModelBuilder(String idHealthInstitution, String name) {
            this.idHealthInstitution = idHealthInstitution;
            this.name = name;

        }

        public HealthInstitutionModelBuilder identityCode(String identityCode) {
            this.identityCode = identityCode;
            return this;
        }

        public HealthInstitutionModelBuilder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }




        public HealthInstitutionModelBuilder state(String state) {
            this.state = state;
            return this;
        }

        public HealthInstitutionModelBuilder city(String city) {
            this.city = city;
            return this;
        }

        public HealthInstitutionModelBuilder street(String street) {
            this.street = street;
            return this;
        }

        public HealthInstitutionModelBuilder neighborhood(String neighborhood) {
            this.neighborhood = neighborhood;
            return this;
        }

        public HealthInstitutionModelBuilder number(String number) {
            this.number = number;
            return this;
        }

        public HealthInstitutionModelBuilder photo(String photo) {
            this.photo = photo;
            return this;
        }

        public HealthInstitutionModelBuilder telephone(String telephone) {
            this.telephone = telephone;
            return this;
        }

        public HealthInstitutionModel build() {
            return new HealthInstitutionModel(this);
        }

    }

    public HealthInstitutionModel(HealthInstitutionModelBuilder builder) {
        this.idHealthInstitution = builder.idHealthInstitution;
        this.identityCode = builder.identityCode;
        this.name = builder.name;
        this.postalCode = builder.postalCode;
        this.state = builder.state;
        this.city = builder.city;
        this.street = builder.street;
        this.neighborhood = builder.neighborhood;
        this.number = builder.number;
        this.photo = builder.photo;
        this.telephone = builder.telephone;
    }

    public String getIdHealthInstitution() {
        return idHealthInstitution;
    }

    public String getIdentityCode() {
        return identityCode;
    }

    public String getName() {
        return name;
    }

    public String getPostalCode() {
        return postalCode;
    }



    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getNumber() {
        return number;
    }

    public String getPhoto() {
        return photo;
    }

    public String getTelephone() {
        return telephone;
    }



}
