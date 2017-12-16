drop schema if exists edgepay_gateway_db cascade;
create schema edgepay_gateway_db;


drop table if exists edgepay_gateway_db.edgepay_fms_rule_entity cascade;
create table edgepay_gateway_db.edgepay_fms_rule_entity
(
	edgepay_fms_rule_entity_id bigserial NOT NULL,
	edgepay_fms_rule_entity_name character varying(50),
	CONSTRAINT edgepay_fms_rule_entity_pk PRIMARY KEY (edgepay_fms_rule_entity_id),
	UNIQUE (edgepay_fms_rule_entity_name)
);


drop table if exists edgepay_gateway_db.edgepay_fms_rule cascade;
create table edgepay_gateway_db.edgepay_fms_rule
(
	edgepay_fms_rule_id bigserial NOT NULL,
	edgepay_fms_rule_entity_id bigint,
	edgepay_fms_rule_access_mode character varying(10),
	edgepay_fms_rule_action character varying(10),
	edgepay_fms_rule_value text,
	edgepay_fms_rule_usage_limit integer,
	edgepay_fms_rule_max_transaction_amount numeric(16,2),
	edgepay_fms_rule_time_period character varying(20),
	edgepay_fms_rule_avs_check_required boolean DEFAULT false,
	edgepay_fms_rule_created_by character varying(50),
	edgepay_fms_rule_creation_time timestamp without time zone,
	edgepay_fms_rule_updated_by character varying(50),
	edgepay_fms_rule_updation_time timestamp without time zone,
	CONSTRAINT edgepay_fms_rule_pk PRIMARY KEY (edgepay_fms_rule_id),
	UNIQUE (edgepay_fms_rule_entity_id,edgepay_fms_rule_value)
);


drop table if exists edgepay_gateway_db.edgepay_fms_merchant cascade;
create table edgepay_gateway_db.edgepay_fms_merchant
(
	edgepay_fms_merchant_sl_id bigserial NOT NULL,
	edgepay_merchant_mid character varying(20),
	CONSTRAINT edgepay_fms_merchant_pk PRIMARY KEY (edgepay_fms_merchant_sl_id)
);


drop table if exists edgepay_gateway_db.edgepay_fms_rule_merchant cascade;
create table edgepay_gateway_db.edgepay_fms_rule_merchant
(
    edgepay_fms_rule_id bigint NOT NULL,
    edgepay_fms_merchant_id bigint NOT NULL,
    CONSTRAINT edgepay_fms_rule_merchant_pk PRIMARY KEY (edgepay_fms_rule_id,edgepay_fms_merchant_id)
);


drop table if exists edgepay_gateway_db.edgepay_fms_transaction cascade;					                                               
create table edgepay_gateway_db.edgepay_fms_transaction
(    
    edgepay_fms_transaction_sl_id bigserial NOT NULL,
    edgepay_fms_transaction_id character varying(100) NOT NULL,
    edgepay_fms_generated_transaction_id character varying(100),
    edgepay_fms_transaction_type character varying(100),
    edgepay_fms_transaction_status character varying(100),
    edgepay_fms_transaction_total_amount  numeric(16,2),
    edgepay_fms_transaction_email character varying(100),
    edgepay_fms_transaction_card_no character varying(100),
    edgepay_fms_transaction_ip character varying(100),
    edgepay_fms_transaction_customername character varying(100),
    edgepay_fms_transaction_zip character varying(100),
    edgepay_fms_transaction_street_address character varying(100),
    edgepay_fms_transaction_city character varying(100),
    edgepay_fms_transaction_state character varying(100),
    edgepay_fms_transaction_geoloc_ip character varying(100),
    edgepay_fms_transaction_device_id character varying(100),
    edgepay_fms_transaction_notes text,
    edgepay_fms_transaction_violated_rules text,
    edgepay_fms_transaction_executed_by character varying(100),
    edgepay_fms_transaction_execution_ts timestamp without time zone,
    CONSTRAINT edgepay_fms_transaction_pkey PRIMARY KEY (edgepay_fms_transaction_sl_id),
    UNIQUE (edgepay_fms_transaction_id)
);