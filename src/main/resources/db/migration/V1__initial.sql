-- cluster_config definition

CREATE TABLE IF NOT EXISTS cluster_config (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                name TEXT DEFAULT ('default_cluster_name'),
                                config TEXT
);

-- sync_config definition

CREATE TABLE  IF NOT EXISTS sync_config (
                             id INTEGER PRIMARY KEY AUTOINCREMENT,
                             sync_type TEXT,
                             auto_sync INTEGER,
                             enable INTEGER,
                             namespace TEXT,
                             pod TEXT,
                             container TEXT,
                             source TEXT,
                             target TEXT
);

CREATE TABLE IF NOT EXISTS text_template (
                                              id INTEGER PRIMARY KEY AUTOINCREMENT,
                                              name TEXT DEFAULT ('template_name'),
                                              content TEXT DEFAULT (''),
                                              type TEXT DEFAULT ('command'),
                                              description TEXT DEFAULT (''),
                                              version INTEGER DEFAULT (1),
                                              template_variables TEXT DEFAULT ('{}')

);

CREATE TABLE IF NOT EXISTS text_template_instance (
                                             id INTEGER PRIMARY KEY AUTOINCREMENT,
                                             template_id INTEGER,
                                             templateVersion INTEGER,
                                             name TEXT DEFAULT ('instance_name'),
                                             content TEXT DEFAULT (''),
                                             description TEXT DEFAULT ('')

);