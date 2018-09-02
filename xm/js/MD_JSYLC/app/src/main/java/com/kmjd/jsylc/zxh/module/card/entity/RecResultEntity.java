package com.kmjd.jsylc.zxh.module.card.entity;

import java.util.List;

public class RecResultEntity {
    private List<OutputEntity> outputs;

    public List<OutputEntity> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<OutputEntity> outputs) {
        this.outputs = outputs;
    }

    public class OutputEntity{
        private String outputLabel;
        private OutputValue outputValue;

        public String getOutputLabel() {
            return outputLabel;
        }

        public void setOutputLabel(String outputLabel) {
            this.outputLabel = outputLabel;
        }

        public OutputValue getOutputValue() {
            return outputValue;
        }

        public void setOutputValue(OutputValue outputValue) {
            this.outputValue = outputValue;
        }

        public class OutputValue{
            private String dataType;

            private String dataValue;

            public String getDataType() {
                return dataType;
            }

            public void setDataType(String dataType) {
                this.dataType = dataType;
            }

            public String getDataValue() {
                return dataValue;
            }

            public void setDataValue(String dataValue) {
                this.dataValue = dataValue;
            }
        }
    }
}
