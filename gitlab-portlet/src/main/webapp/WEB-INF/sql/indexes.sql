create index IX_EBE0C8CB on gitlab_Commit (authorName);
create index IX_A5BCDD89 on gitlab_Commit (authorName, projectName);
create index IX_4B2A4F73 on gitlab_Commit (projectName);

create index IX_76EAEFC on gitlab_Contributor (name);