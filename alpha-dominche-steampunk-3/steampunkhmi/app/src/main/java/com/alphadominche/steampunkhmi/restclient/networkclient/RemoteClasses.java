package com.alphadominche.steampunkhmi.restclient.networkclient;


public class RemoteClasses {
    //	public static class RemoteAgitation {
//		public Long id;
//		public Long stack;
//		public int duration;
//		public int start_time;
//		public Long local_machine_identifier;
//		public String machine_mac_address;
//		
//		public RemoteAgitation(final Long id, final Long local_id, final Long stack, final int duration, final int start_time){
//			this.id = id;
//			this.local_machine_identifier = local_id;
//			this.stack = stack;
//			this.duration = duration;
//			this.start_time = start_time;
//		}
//	}
//	public static class RemoteStack {
//		public Long id;
//		public Long recipe;
//		public int order;
//		public double volume;
//		public int start_time;
//		public int duration;
//		public double temperature;
//		public double vacuum_break;
//		public int pull_down_time;
//		public RemoteAgitation[] agitationcycle_set;
//		public Long local_machine_identifier;
//		
//		public RemoteStack(final Long id,final Long local_id, final Long recipe,
//				final int order, final double volume,
//				final int start_time, final int duration,final double temperature,
//				final double vacuum_break, final int pull_down_time) {
//			this.id = id;
//			this.local_machine_identifier = local_id;
//			this.recipe = recipe;
//			this.order = order;
//			this.volume = volume;
//			this.start_time = start_time;
//			this.duration = duration;
//			this.temperature = temperature;
//			this.vacuum_break = vacuum_break;
//			this.pull_down_time = pull_down_time;
//			
//		}
//	}
    public static class RemoteRecipe {
        public Long id;
        public String name;
        public int type;
        public Long steampunkuser;
        public boolean published;
        public double grams;
        public double teaspoons;
        public double grind;
        public int filter;
        public boolean deleted;
        public String stacks;
        public String uuid;
        //		public RemoteStack[] stack_set;
        public Long local_machine_identifier;
        public String machine_mac_address;

        public RemoteRecipe(final Long id, final Long localId, final String name,
                            final int type, final Long steampunkuser,
                            final boolean published, final double grams,
                            final double teaspoons, final double grind, final int filter, String stacks, String uuid) {
            this.id = id;
            this.local_machine_identifier = localId;
            this.name = name;
            this.type = type;
            this.steampunkuser = steampunkuser;
            this.published = published;
            this.grams = grams;
            this.teaspoons = teaspoons;
            this.grind = grind;
            this.filter = filter;
            this.stacks = stacks;
            this.uuid = uuid;
        }
    }

    public static class RemoteGrind {
        public int id;
        public String name;
        public String description;
        public String icon;
    }

    public static class RemoteLog {
        public Integer id;
        public Long machine;
        public Long user;
        public String date;
        public Integer crucible;
        public String recipe;
        public Integer severity;
        public Integer type;
        public String message;

        public RemoteLog() {
        }

        public RemoteLog(Integer id, Long machine, Long userId, String date,
                         Integer crucible, String recipe, Integer severity,
                         Integer type, String message) {
            this.id = id;
            this.machine = machine;
            this.user = userId;
            this.date = date;
            this.recipe = recipe;
            this.crucible = crucible;
            this.severity = severity;
            this.type = type;
            this.message = message;
        }
    }

    public static class RemoteLogin {
        public String username;
        public String password;
        public String token;
        public String type;
        public Long id;
        public Long steampunkuserId;
        public String email;
        public String address;
        public String city;
        public String state;
        public String country;
        public String postal_code;
        public Boolean public_status;

        public RemoteLogin(String username, String password) {
            this.username = username;
            this.password = password;
            this.token = null;
            this.type = null;
            this.id = (long) 0;
            this.steampunkuserId = (long) 0;
            this.email = null;
            this.address = null;
            this.city = null;
            this.state = null;
            this.country = null;
            this.postal_code = null;
            this.public_status = null;
        }
    }

    public static class RemoteMachine {
        public Long id;
        public String serial_number;
        public String model;
        public Integer crucible_count;
        public String PIN;
        public String company;
        public Integer boiler_temp;
        public Integer rinse_temp;
        public Integer rinse_volume;
        public Integer elevation;

        public RemoteMachine() {
        }
    }

    public static class RemoteRoaster {
        public Long id;
        public String first_name;
        public String last_name;
        public String username;
        public Long steampunkuser;
        public Integer subscribed_to;


        public RemoteRoaster() {
        }

        public RemoteRoaster(String first_name, String last_name, String username, Long id, Long steampunkuser, Integer subscribed_to) {
            this.first_name = first_name;
            this.last_name = last_name;
            this.username = username;
            this.id = id;
            this.steampunkuser = steampunkuser;
            this.subscribed_to = subscribed_to;
        }
    }

    public static class RemoteFavorite {
        public String recipe_uuid;
        public Long user;
        public String uuid;

        public RemoteFavorite() {
        }

        public RemoteFavorite(String recipe_uuid, long user, String uuid) {
            this.recipe_uuid = recipe_uuid;
            this.user = user;
            this.uuid = uuid;
        }
    }

    public static class PasswordReset {
        public String identifier;
        public String machine_id;
    }

    public static class PasswordChange {
        public String old;
        public String new_pass;
    }

    public static class AvailableUpdate {
        public int version;
    }

    public static class Device {
        public Integer id;
        public String name;
        public Integer platform;
        public Integer[] capabilities;

        public Device(String name, int platform) {
            this.name = name;
            this.platform = platform;
        }

    }

    public static class RemoteUser {
        public String first_name;
        public String last_name;
        public String email;
        public String username;
    }

    public static class RemoteSteamPunkUser {
        public String address;
        public String city;
        public String state;
        public String country;
        public String postal_code;
    }

    public static class UserIdMapping {
        public long id;
        public long steampunkuser;
    }
}
