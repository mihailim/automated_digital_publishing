# Copyright (C) 2014  Stephan Kreutzer
#
# This file is part of automated_digital_publishing.
#
# automated_digital_publishing is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License version 3 or any later version,
# as published by the Free Software Foundation.
#
# automated_digital_publishing is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Affero General Public License 3 for more details.
#
# You should have received a copy of the GNU Affero General Public License 3
# along with automated_digital_publishing. If not, see <http://www.gnu.org/licenses/>.



.PHONY: all odt2all1_config_edit2 clean



all: odt2all1_config_edit2
odt2all1_config_edit2: odt2all1_config_edit2.class



odt2all1_config_edit2.class: odt2all1_config_edit2.java
	javac odt2all1_config_edit2.java

clean:
	rm -f ./*.class

