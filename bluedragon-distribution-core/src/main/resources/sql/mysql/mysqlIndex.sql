
create UNIQUE index PK_ABNORMAL_ORDER on ABNORMAL_ORDER (SYSTEM_ID);
create UNIQUE index PK_ALERT_ID on ALERT_CONFIG (ALERT_ID);
create UNIQUE index PK_BATCHINFO on BATCHINFO (ID);
create  index IND_BATCH_SEND_BCODE on BATCH_SEND (BATCH_CODE);
create  index IND_BATCH_SEND_SCODE on BATCH_SEND (SEND_CODE);
create UNIQUE index PK_BATCH_SEND on BATCH_SEND (ID);
create  index IND_BOX_BC_P on BOX (BOX_CODE);
create  index IND_BOX_CRSITE_P on BOX (CREATE_SITE_CODE,RECEIVE_SITE_CODE);
create UNIQUE index UNQ_BOX_P on BOX (BOX_ID,CREATE_TIME);
create  index IND_CEN_CONFIRM_CSTAT_D_P on CEN_CONFIRM (CONFIRM_STATUS);
create  index IND_CEN_CONFIRM_PCODE_P on CEN_CONFIRM (PACKAGE_BARCODE);
create  index IND_CEN_CONFIRM_SITE_CT on CEN_CONFIRM (CREATE_SITE_CODE,CREATE_TIME);
create  index IND_CEN_CONFIRM_WCODE_P on CEN_CONFIRM (WAYBILL_CODE);
create UNIQUE index UNQ_CEN_CONFIRM_P on CEN_CONFIRM (CONFIRM_ID,CREATE_TIME);
create UNIQUE index PK_CLIENT_CONFIG on CLIENT_CONFIG (CONFIG_ID);
create UNIQUE index PK_CLIENT_CONFIG_HISTORY on CLIENT_CONFIG_HISTORY (CONFIG_HISTORY_ID);
create UNIQUE index PK_CLIENT_VERSION on CLIENT_VERSION (VERSION_ID);
create UNIQUE index SYS_C0011875 on CROSS_SORTING (ID);
create UNIQUE index PK_DBS_OBJECTID_ID on DBS_OBJECTID (ID);
create  index IND_DEPARTURE_CAR_CCODE on DEPARTURE_CAR (SHIELDS_CAR_CODE);
create  index IND_DEPARTURE_CAR_FPRINT on DEPARTURE_CAR (FINGERPRINT);
create UNIQUE index PK_DEPARTURE_CAR on DEPARTURE_CAR (DEPARTURE_CAR_ID);
create  index IND_DEPARTURE_SEND_SCODE on DEPARTURE_SEND (SEND_CODE);
create UNIQUE index PK_DEPARTURE_SEND on DEPARTURE_SEND (DEPARTURE_SEND_ID);
create UNIQUE index PK_FBARCODE on FBARCODE (FBARCODE_ID);
create  index IND_INSPECTION_BCODE_PL on INSPECTION (BOX_CODE);
create  index IND_INSPECTION_CS_CTIME on INSPECTION (CREATE_SITE_CODE,CREATE_TIME);
create  index IND_INSPECTION_PCODE_D on INSPECTION (PACKAGE_BARCODE);
create  index IND_INSPECTION_QNO_PL on INSPECTION (QUEUE_NO);
create  index IND_INSPECTION_STATUS_P on INSPECTION (INSPECTION_STATUS);
create  index IND_INSPECTION_WCODE_D on INSPECTION (WAYBILL_CODE);
create UNIQUE index UNQ_INSPECTION_P on INSPECTION (CREATE_TIME,INSPECTION_ID);
create  index IND_INSPECTION_E_C_BCODE_P on INSPECTION_E_C (BOX_CODE);
create  index IND_INSPECTION_E_C_PCODE_P on INSPECTION_E_C (PACKAGE_BARCODE);
create  index INSPECTION_E_C_CI_P on INSPECTION_E_C (CREATE_SITE_CODE,INSPECTION_E_C_TYPE);
create  index INSPECTION_E_C_WC_P on INSPECTION_E_C (WAYBILL_CODE);
create UNIQUE index UNQ_INSPECTION_E_C_P on INSPECTION_E_C (CHECK_ID,CREATE_TIME);
create  index IDX_LOADBILL_BOXCODE on LOAD_BILL (BOX_CODE);
create  index IDX_LOADBILL_LD_OD_WID on LOAD_BILL (LOAD_ID,ORDER_ID,WAREHOUSE_ID);
create  index IDX_LOADBILL_SENDCODE on LOAD_BILL (SEND_CODE);
create  index IDX_LOADBILL_SENDTIME on LOAD_BILL (SEND_TIME);
create  index IND_LOAD_BILL_PCODE on LOAD_BILL (PACKAGE_BARCODE);
create  index IND_LOAD_BILL_WCODE on LOAD_BILL (WAYBILL_CODE);
create UNIQUE index LOSS_ORDER_PK on LOSS_ORDER (SYSTEM_ID);
create UNIQUE index UNQ_OFFLINE_LOG_ID on OFFLINE_LOG (OFFLINE_LOG_ID,CREATE_TIME);
create  index IND_OPERATION_LOG_0_BCODE_P on OPERATION_LOG_0 (BOX_CODE);
create  index IND_OPERATION_LOG_0_PCODE_P on OPERATION_LOG_0 (PACKAGE_CODE);
create  index IND_OPERATION_LOG_0_WCODE_P on OPERATION_LOG_0 (WAYBILL_CODE);
create  index IND_OPERATION_L_PCODE_PL on OPERATION_LOG_0 (PICKUP_CODE);
create UNIQUE index UNQ_OPERATION_LOG_0_P on OPERATION_LOG_0 (LOG_ID,CREATE_TIME);
create UNIQUE index PK_PARTNER_WAYBILL on PARTNER_WAYBILL (RELATION_ID);
create  index IND_PICK_WARE_FINGPT on PICK_WARE (FINGERPRINT);
create UNIQUE index PK_PICKWARE on PICK_WARE (PICKWARE_ID);
create UNIQUE index PK_POP_ABNORMAL on POP_ABNORMAL (ABNORMAL_ID);
create UNIQUE index PK_POP_ABNORMAL_DETAIL on POP_ABNORMAL_DETAIL (ABNORMAL_DETAIL_ID);
create UNIQUE index PK_POP_ABNORMAL_ORDER on POP_ABNORMAL_ORDER (ID);
create  index IND_POP_PICKUP_CID_OTIME on POP_PICKUP (CREATE_SITE_CODE,OPERATE_TIME);
create  index IND_POP_PICKUP_WCODE on POP_PICKUP (WAYBILL_CODE);
create UNIQUE index PK_POP_PICKUP on POP_PICKUP (PICKUP_ID);
create  index IND_POP_PRINT_WC_P on POP_PRINT (WAYBILL_CODE);
create UNIQUE index UNQ_POP_PRINT_P on POP_PRINT (CREATE_TIME,ID);
create  index IND_POP_QUEUE_QNO on POP_QUEUE (QUEUE_NO);
create UNIQUE index UNQ_POP_QUEUE_ID on POP_QUEUE (ID,CREATE_TIME);
create  index IND_POP_RECEIVE_FPRINT on POP_RECEIVE (FINGERPRINT);
create  index IND_POP_RECEIVE_WCODE on POP_RECEIVE (WAYBILL_CODE);
create UNIQUE index UNQ_POP_RECEIVE_ID on POP_RECEIVE (SYSTEM_ID,CREATE_TIME);
create  index IND_POP_SIGNIN_QNO on POP_SIGNIN (QUEUE_NO);
create  index IND_POP_SIGNIN_TWCODE on POP_SIGNIN (THIRD_WAYBILL_CODE);
create UNIQUE index UNQ_POP_SIGNIN_ID on POP_SIGNIN (ID,CREATE_TIME);
create  index IND_RE_WAYBILL_PCODE on REASSIGN_WAYBILL (PACKAGE_BARCODE);
create  index IND_RE_WAYBILL_WCODE on REASSIGN_WAYBILL (WAYBILL_CODE);
create UNIQUE index PK_REASSIGN_WAYBILL on REASSIGN_WAYBILL (REASSIGN_WAYBILL_ID);
create  index IND_RECEIVE_BCODE on RECEIVE (BOX_CODE);
create UNIQUE index UNQ_RECEIVE_P on RECEIVE (RECEIVE_ID,CREATE_TIME);
create UNIQUE index PK_REVERSE_LABEL on REVERSE_LABEL (ID);
create  index IND_REVERSE_RECEIVE_PCODE on REVERSE_RECEIVE (PACKAGE_BARCODE);
create UNIQUE index REVERSE_RECEIVE_PK on REVERSE_RECEIVE (SYSTEM_ID);
create UNIQUE index PK_REVERSE_REJECT_NEW on REVERSE_REJECT (SYSTEM_ID);
create UNIQUE index PK_REVERSE_REJECT on REVERSE_REJECT_BAK_20150421 (SYSTEM_ID);
create  index IND_REVERSE_SPARE_CODE_NEW on REVERSE_SPARE (SPARE_CODE);
create  index IND_REVERSE_SPARE_STCODE_N on REVERSE_SPARE (SPARE_TRAN_CODE);
create UNIQUE index PK_REVERSE_SPARE_NEW on REVERSE_SPARE (SYSTEM_ID);
create  index IND_REVERSE_SPARE_CODE on REVERSE_SPARE_BAK_0730 (SPARE_CODE);
create  index IND_REVERSE_SPARE_STCODE on REVERSE_SPARE_BAK_0730 (SPARE_TRAN_CODE);
create UNIQUE index PK_REVERSE_SPARE on REVERSE_SPARE_BAK_0730 (SYSTEM_ID);
create  index IND_SEAL_BOX_BCODE_PL on SEAL_BOX (BOX_CODE);
create  index IND_SEAL_BOX_SCODE_PL on SEAL_BOX (SEAL_CODE);
create UNIQUE index UNQ_SEAL_BOX_P on SEAL_BOX (SYSTEM_ID,CREATE_TIME);
create  index IND_SEAL_VEHICLESC on SEAL_VEHICLE (SEAL_CODE);
create UNIQUE index PK_SEAL_VEHICLE on SEAL_VEHICLE (SYSTEM_ID);
create  index IND_SEND_D_BCODE_P on SEND_D (BOX_CODE);
create  index IND_SEND_D_PB_P on SEND_D (PACKAGE_BARCODE);
create  index IND_SEND_D_SCODE_P on SEND_D (SEND_CODE);
create  index IND_SEND_D_SITE_OT_PL on SEND_D (CREATE_SITE_CODE,RECEIVE_SITE_CODE,OPERATE_TIME);
create  index IND_SEND_D_STAT_CT on SEND_D (SENDD_STATUS,CREATE_TIME);
create  index IND_SEND_D_WCODE_P on SEND_D (WAYBILL_CODE);
create UNIQUE index UNQ_SEND_D_P on SEND_D (CREATE_TIME,SEND_D_ID);
create  index IND_SEND_M_BCODE_BCODE_P on SEND_M (BOX_CODE);
create  index IND_SEND_M_BCODE_CSITE_CODE on SEND_M (CREATE_SITE_CODE,RECEIVE_SITE_CODE,BOX_CODE);
create  index IND_SEND_M_BCODE_CSITE_OT on SEND_M (CREATE_SITE_CODE,RECEIVE_SITE_CODE,OPERATE_TIME);
create  index IND_SEND_M_SCAR_ID on SEND_M (SHIELDS_CAR_ID);
create  index IND_SEND_M_SC_P on SEND_M (SEND_CODE);
create UNIQUE index UNQ_SEND_M_P on SEND_M (CREATE_TIME,SEND_M_ID);
create  index IND_SEND_QUERY_SENDCODE on SEND_QUERY (SEND_CODE);
create UNIQUE index PK_SEND_QUERY on SEND_QUERY (ID);
create UNIQUE index PK_SHIELDS_ERROR on SHIELDS_ERROR (ERROR_ID);
create  index IND_SORTING_BCODE_P on SORTING (BOX_CODE);
create  index IND_SORTING_BSCODE_P on SORTING (BSEND_CODE);
create  index IND_SORTING_PCODE_P on SORTING (PACKAGE_CODE);
create  index IND_SORTING_WCODE_P on SORTING (WAYBILL_CODE);
create UNIQUE index UNQ_SORTING_P on SORTING (CREATE_TIME,SORTING_ID);
create UNIQUE index PK_SORTING_EC on SORTING_EC (ID);
create  index IND_SORTING_RET_PCODE on SORTING_RET (PACKAGE_CODE);
create  index IND_SORTING_RET_WCODE on SORTING_RET (WAYBILL_CODE);
create UNIQUE index PK_SORTING_RET on SORTING_RET (SORTING_RET_ID);
create UNIQUE index SPARE_PK on SPARE (SPARE_ID);
create UNIQUE index PK_SPARE_SALE on SPARE_SALE (ID);
create UNIQUE index PK_SYSCONFIG on SYSCONFIG (CONFIG_ID);
create UNIQUE index UNQ_SYSTEM_LOG on SYSTEM_LOG (ID,CREATE_TIME);
create UNIQUE index PK_TASK_BOUNDARY on TASK_BOUNDARY (TASK_ID);
create  index IND_TASK_FAILQUEUE_BID_P on TASK_FAILQUEUE (BUSI_ID);
create  index IND_TASK_FAILQUEUE_FS_P on TASK_FAILQUEUE (FAIL_STATUS);
create UNIQUE index UNQ_TASK_FAILQUEUE_ID_P on TASK_FAILQUEUE (FAILQUEUE_ID,DB_ACCEPT_TIME);
create  index IND_TASK_HANDOVER_FP on TASK_HANDOVER (FINGERPRINT);
create  index IND_TASK_HANDOVER_KWORD on TASK_HANDOVER (KEYWORD2);
create  index IND_TASK_HANDOVER_STAT on TASK_HANDOVER (TASK_STATUS);
create UNIQUE index UNQ_TASK_HANDOVER on TASK_HANDOVER (TASK_ID,CREATE_TIME);
create  index IND_TASK_INSPECTION_FP_P on TASK_INSPECTION (FINGERPRINT);
create  index IND_TASK_INSPECTION_STATUS_P on TASK_INSPECTION (TASK_STATUS);
create UNIQUE index UNQ_TASK_INSPECTION_ID_P on TASK_INSPECTION (TASK_ID,DB_ACCEPT_TIME);
create  index IND_TASK_OFFLINE_STAT on TASK_OFFLINE (TASK_STATUS);
create UNIQUE index UNQ_TASK_OFFLINE_ID on TASK_OFFLINE (TASK_ID,CREATE_TIME);
create UNIQUE index UNQ_TASK_PDA_ID_P on TASK_PDA (TASK_ID,DB_ACCEPT_TIME);
create  index IND_TASK_POP_KEYWORD1 on TASK_POP (KEYWORD1);
create  index IND_TASK_POP_STATUS_P on TASK_POP (TASK_STATUS);
create UNIQUE index UNQ_TASK_POP_ID_P on TASK_POP (TASK_ID,DB_ACCEPT_TIME);
create  index IND_TASK_POP_RE_CNT_STAT on TASK_POP_RECIEVE_COUNT (OWN_SIGN,TASK_STATUS);
create  index IND_TASK_POP_RE_CNT_WCODE on TASK_POP_RECIEVE_COUNT (WAYBILL_CODE);
create UNIQUE index PK_TASK_POP_RECIEVE_COUNT on TASK_POP_RECIEVE_COUNT (TASK_ID);
create UNIQUE index PK_TASK_RECEIVE_EXCEPTION on TASK_RECEIVE_EXCEPTION (TASK_ID);
create UNIQUE index PK_TASK_RECEIVE_PICKUP on TASK_RECEIVE_PICKUP (TASK_ID);
create  index IND_TASK_RECEIVE_RECEIVE_FS_P on TASK_RECEIVE_RECEIVE (TASK_STATUS,TASK_TYPE);
create UNIQUE index PK_TASK_RECEIVE_RECEIVE on TASK_RECEIVE_RECEIVE (TASK_ID);
create  index IND_TASK_REVERSE_TS_P on TASK_REVERSE (TASK_STATUS);
create UNIQUE index UNQ_TASK_REVERSE_ID_P on TASK_REVERSE (TASK_ID,DB_ACCEPT_TIME);
create  index IND_TASK_SEND_FP_P on TASK_SEND (FINGERPRINT);
create  index IND_TASK_SEND_TS_P on TASK_SEND (TASK_STATUS);
create UNIQUE index UNQ_TASK_SEND_ID_P on TASK_SEND (TASK_ID,DB_ACCEPT_TIME);
create  index IND_TASK_SORTING_BCODE_P on TASK_SORTING (BOX_CODE);
create  index IND_TASK_SORTING_FP_P on TASK_SORTING (FINGERPRINT);
create  index IND_TASK_SORTING_STATUS_P on TASK_SORTING (TASK_STATUS);
create UNIQUE index UNQ_TASK_SORTING_ID_P on TASK_SORTING (TASK_ID,DB_ACCEPT_TIME);
create UNIQUE index PK_TASK_SORTING_EC on TASK_SORTING_EC (TASK_ID);
create  index IND_TASK_WAYBILL_FP_P on TASK_WAYBILL (FINGERPRINT);
create  index IND_TASK_WAYBILL_TS_P on TASK_WAYBILL (TASK_STATUS);
create UNIQUE index UNQ_TASK_WAYBILL_ID_P on TASK_WAYBILL (TASK_ID,DB_ACCEPT_TIME);
create  index IND_TASK_WEIGHT_STAT on TASK_WEIGHT (TASK_STATUS);
create UNIQUE index UNQ_TASK_WEIGHT on TASK_WEIGHT (TASK_ID,CREATE_TIME);
create UNIQUE index PK_TURNOVERBOX on TURNOVERBOX (TURNOVERBOX_ID);
