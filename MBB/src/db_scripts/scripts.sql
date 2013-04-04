drop TABLE `rating` ;


CREATE TABLE `rating` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `info_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lookfeel` int(11) NOT NULL,
  `usability` int(11) NOT NULL,
  `quality` int(11) NOT NULL,
  PRIMARY KEY (`id`)
);

drop TABLE `mbb_data` ;

CREATE TABLE `mbb_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `info_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `info_deadline_time` timestamp,
  `teacher_id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL,
  `info_type` varchar(4000) COLLATE utf8_unicode_ci NOT NULL,
  `info_details` varchar(4000) COLLATE utf8_unicode_ci NOT NULL,
  `retired` char(1) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
);


--INSERT INTO `mbb_data` (`info_time`,`teacher_id`,`course_id`,`info_type`,`info_details`) VALUES ('2012-12-11 01:33:46'999,777,'Announcement','ann1');
--INSERT INTO `mbb_data` (`info_time`,`teacher_id`,`course_id`,`info_type`,`info_details`) VALUES ('2012-12-11 01:36:09',999,777,'Announcement','ann2',);
--INSERT INTO `mbb_data` (`info_time`,`info_deadline_time`,`teacher_id`,`course_id`,`info_type`,`info_details`) VALUES 
--('2012-12-11 01:38:12','2012-12-20 23:32:00',999,777,'Assignment','assign1');
--INSERT INTO `mbb_data` (`info_time`,`info_deadline_time`,`teacher_id`,`course_id`,`info_type`,`info_details`) VALUES 
--('2012-12-11 01:38:15','2012-12-20 23:33:00',999,777,'Assignment','assign12);





