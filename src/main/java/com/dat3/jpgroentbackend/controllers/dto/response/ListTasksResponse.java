package com.dat3.jpgroentbackend.controllers.dto.response;

import com.dat3.jpgroentbackend.model.BatchLocation;
import com.dat3.jpgroentbackend.model.PlantType;
import com.dat3.jpgroentbackend.model.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListTasksResponse {

    public static class TaskResponse{
        private final int batchId;
        private final String plantTypeName;
        private final Task.Category category;
        private int amount;
        private final LocalDate dueDate;

        public TaskResponse(int batchId, String plantTypeName, Task.Category category, int amount, LocalDate dueDate) {
            this.batchId = batchId;
            this.plantTypeName = plantTypeName;
            this.category = category;
            this.amount = amount;
            this.dueDate = dueDate;
        }

        public void addAmount(int amount){
            this.amount += amount;
        }

        public int getBatchId() {
            return batchId;
        }

        public String getPlantTypeName() {
            return plantTypeName;
        }

        public Task.Category getCategory() {
            return category;
        }

        public int getAmount() {
            return amount;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public boolean canBeCombined(TaskResponse otherTaskResponse){
            boolean sameBatchId = this.batchId == otherTaskResponse.batchId;
            boolean sameCategory = this.category == otherTaskResponse.category;
            boolean samePlantType = this.plantTypeName.equals(otherTaskResponse.plantTypeName);
            boolean sameDueDate = this.dueDate.equals(otherTaskResponse.dueDate);

            return sameBatchId & sameCategory & samePlantType & sameDueDate;
        }
    }

    private final List<TaskResponse> taskResponses = new ArrayList<>();

    public ListTasksResponse(Map<BatchLocation, List<Task>> tasksByLocation){
        for(Map.Entry<BatchLocation, List<Task>> entry : tasksByLocation.entrySet()){
            BatchLocation batchLocation = entry.getKey();
            List<Task> tasks = entry.getValue();

            for(Task task : tasks){
                TaskResponse taskResponse = new TaskResponse(batchLocation.batch.id, batchLocation.batch.plantType.getName(), task.category, batchLocation.amount, task.dueDate);

                //Combine matching task or add a new task
                boolean taskWasCombined = false;
                for(TaskResponse existingTaskResponse : taskResponses){
                    if(existingTaskResponse.canBeCombined(taskResponse)){
                        existingTaskResponse.addAmount(taskResponse.amount);
                        taskWasCombined = true;
                        break;
                    }
                }
                if(!taskWasCombined){
                    taskResponses.add(taskResponse);
                }

            }
        }
    }

    public List<TaskResponse> getTaskResponses(){
        return taskResponses;
    }
}
